package br.com.oncast.ontrack.client.services.annotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ContextProviderServiceImpl.ContextChangeListener;
import br.com.oncast.ontrack.client.services.notification.ClientNotificationService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeDetailAddedEvent;
import br.com.oncast.ontrack.client.ui.places.details.DetailPlace;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationRemoveAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.exceptions.AnnotationNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.AnnotatedSubjectIdsRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.AnnotatedSubjectIdsResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.AnnotationsListRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.AnnotationsListResponse;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

public class AnnotationServiceImpl implements AnnotationService {

	private final ActionExecutionService actionExecutionService;
	private final ContextProviderService contextProviderService;
	private final AuthenticationService authenticationService;
	private final ClientNotificationService clientNotificationService;
	private final ApplicationPlaceController applicationPlaceController;
	private final DispatchService dispatchService;
	private Set<UUID> annotatedSubjectIds;
	private ActionExecutionListener actionExecutionListener;
	private UUID currentProjectId;
	private final EventBus eventBus;
	private final List<AnnotationModificationListener> annotationCreationListeners;

	public AnnotationServiceImpl(final ActionExecutionService actionExecutionService, final ContextProviderService contextProviderService,
			final AuthenticationService authenticationService, final ClientNotificationService clientNotificationService,
			final ApplicationPlaceController applicationPlaceController, final DispatchService dispatchService, final EventBus eventBus) {
		this.actionExecutionService = actionExecutionService;
		this.contextProviderService = contextProviderService;
		this.authenticationService = authenticationService;
		this.clientNotificationService = clientNotificationService;
		this.applicationPlaceController = applicationPlaceController;
		this.dispatchService = dispatchService;
		this.eventBus = eventBus;

		this.annotationCreationListeners = new ArrayList<AnnotationModificationListener>();

		contextProviderService.addContextLoadListener(new ContextChangeListener() {
			@Override
			public void onProjectChanged(final UUID projetId) {
				setCurrentProject(projetId);
			}

		});

		actionExecutionService.addActionExecutionListener(getActionExecutionListener());
	}

	@Override
	public boolean hasDetails(final UUID subjectId) {
		final ProjectContext context = contextProviderService.getCurrentProjectContext();

		return context.hasChecklistsFor(subjectId) || hasAnnotationsFor(subjectId);
	}

	private boolean hasAnnotationsFor(final UUID subjectId) {
		return annotatedSubjectIds != null && annotatedSubjectIds.contains(subjectId);
	}

	@Override
	public void loadAnnotatedSubjectIds(final AsyncCallback<Set<UUID>> callback) {
		if (!hasCurrentProject()) {
			callback.onFailure(new IllegalStateException("You need to load the project first."));
			return;
		}

		if (isAnnotatedSubjectIdsAvailable()) {
			callback.onSuccess(getAnnotatedSubjectIds());
			return;
		}

		dispatchService.dispatch(new AnnotatedSubjectIdsRequest(getCurrentProjectId()), new DispatchCallback<AnnotatedSubjectIdsResponse>() {
			@Override
			public void onSuccess(final AnnotatedSubjectIdsResponse result) {
				setAnnotatedSubjectIds(result.getAnnotatedSubjectIds());
				callback.onSuccess(getAnnotatedSubjectIds());
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

	private HashSet<UUID> getAnnotatedSubjectIds() {
		return new HashSet<UUID>(annotatedSubjectIds);
	}

	@Override
	public void showAnnotationsFor(final UUID subjectId) {
		applicationPlaceController.goTo(new DetailPlace(getCurrentProjectId(), subjectId, applicationPlaceController.getCurrentPlace()));
	}

	@Override
	public void createAnnotationFor(final UUID subjectId, final String message, final UUID attachmentId) {
		doUserAction(new AnnotationCreateAction(subjectId, message, attachmentId));
	}

	@Override
	public void toggleVote(final UUID subjectId, final UUID annotationId) {
		try {
			AnnotationAction action;
			if (hasVoted(annotationId, subjectId)) action = new AnnotationVoteRemoveAction(annotationId, subjectId);
			else action = new AnnotationVoteAction(annotationId, subjectId);
			doUserAction(action);
		}
		catch (final Exception e) {
			clientNotificationService.showError(e.getMessage());
		}
	}

	@Override
	public void deleteAnnotation(final UUID subjectId, final UUID annotationId) {
		doUserAction(new AnnotationRemoveAction(subjectId, annotationId));
	}

	private boolean hasVoted(final UUID annotationId, final UUID subjectId) throws AnnotationNotFoundException {
		final Annotation annotation = contextProviderService.getCurrentProjectContext().findAnnotation(subjectId, annotationId);
		return annotation.hasVoted(authenticationService.getCurrentUser());
	}

	private void doUserAction(final AnnotationAction action) {
		actionExecutionService.onUserActionExecutionRequest(action);
	}

	private ActionExecutionListener getActionExecutionListener() {
		if (actionExecutionListener == null) actionExecutionListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context,
					final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet, final boolean isUserAction) {

				final UUID referenceId = action.getReferenceId();

				final boolean isCreationAction = action instanceof AnnotationCreateAction;
				if (isCreationAction) {
					annotatedSubjectIds.add(referenceId);
					updateScopeTree(action, context);
				}

				if (isCreationAction || action instanceof AnnotationRemoveAction) {
					if (actionContext.getUserEmail().equals(authenticationService.getCurrentUser().getEmail())) return;

					notifyAnnotationModification(currentProjectId, referenceId, actionContext.getUserEmail(), isCreationAction);
				}
			}

			private void notifyAnnotationModification(final UUID currentProjectId, final UUID referenceId, final String authorEmail, final boolean isCreation) {
				for (final AnnotationModificationListener listener : annotationCreationListeners) {
					listener.onAnnotationModification(currentProjectId, referenceId, authorEmail, isCreation);
				}
			}

			private void updateScopeTree(final ModelAction action, final ProjectContext context) {
				try {
					final Scope scope = context.findScope(action.getReferenceId());
					eventBus.fireEvent(new ScopeDetailAddedEvent(scope));
				}
				catch (final ScopeNotFoundException e) {
					// It's not scope so don't need to update the view
				}
			}
		};
		return actionExecutionListener;
	}

	private UUID getCurrentProjectId() {
		return currentProjectId;
	}

	private void setAnnotatedSubjectIds(final Set<UUID> annotatedSubjectIds) {
		this.annotatedSubjectIds = annotatedSubjectIds;
	}

	@Override
	public boolean isAnnotatedSubjectIdsAvailable() {
		return annotatedSubjectIds != null;
	}

	private void setCurrentProject(final UUID projetId) {
		if (currentProjectId == projetId) return;

		currentProjectId = projetId;
		clearLoadedData();
	}

	private void clearLoadedData() {
		annotatedSubjectIds = null;
	}

	private boolean hasCurrentProject() {
		return currentProjectId != null;
	}

	@Override
	public void loadAnnotationsFor(final UUID subjectId, final AsyncCallback<List<Annotation>> callback) {
		if (!annotatedSubjectIds.contains(subjectId)) {
			callback.onSuccess(new ArrayList<Annotation>());
			return;
		}
		dispatchService.dispatch(new AnnotationsListRequest(getCurrentProjectId(), subjectId), new DispatchCallback<AnnotationsListResponse>() {

			@Override
			public void onSuccess(final AnnotationsListResponse result) {
				callback.onSuccess(result.getAnnotationsList());
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

	public interface AnnotationModificationListener {

		void onAnnotationModification(UUID projectId, UUID subjectId, String authorEmail, boolean isCreation);

	}

	@Override
	public void addAnnotationCreationListener(final AnnotationModificationListener listener) {
		if (this.annotationCreationListeners.contains(listener)) return;

		this.annotationCreationListeners.add(listener);
	}

	@Override
	public void removeAnnotationCreationListener(final AnnotationModificationListener listener) {
		this.annotationCreationListeners.remove(listener);
	}

}
