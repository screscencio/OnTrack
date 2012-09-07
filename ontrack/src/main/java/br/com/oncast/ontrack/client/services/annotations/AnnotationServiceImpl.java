package br.com.oncast.ontrack.client.services.annotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeDetailChangeEvent;
import br.com.oncast.ontrack.client.ui.places.details.DetailPlace;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationDeprecateAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationRemoveAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationRemoveDeprecationAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentCreateAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentSolveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.web.bindery.event.shared.EventBus;

public class AnnotationServiceImpl implements AnnotationService {

	private final ActionExecutionService actionExecutionService;
	private final ContextProviderService contextProviderService;
	private final AuthenticationService authenticationService;
	private final ApplicationPlaceController applicationPlaceController;
	private ActionExecutionListener actionExecutionListener;
	private final EventBus eventBus;
	private final List<AnnotationModificationListener> annotationCreationListeners;

	public AnnotationServiceImpl(final ActionExecutionService actionExecutionService, final ContextProviderService contextProviderService,
			final AuthenticationService authenticationService, final ApplicationPlaceController applicationPlaceController, final EventBus eventBus) {
		this.actionExecutionService = actionExecutionService;
		this.contextProviderService = contextProviderService;
		this.authenticationService = authenticationService;
		this.applicationPlaceController = applicationPlaceController;
		this.eventBus = eventBus;

		this.annotationCreationListeners = new ArrayList<AnnotationModificationListener>();

		actionExecutionService.addActionExecutionListener(getActionExecutionListener());
	}

	@Override
	public boolean hasDetails(final UUID subjectId) {
		final ProjectContext context = contextProviderService.getCurrentProjectContext();

		return context.hasChecklistsFor(subjectId) || hasAnnotationsFor(subjectId);
	}

	private boolean hasAnnotationsFor(final UUID subjectId) {
		return getCurrentContext().hasAnnotationsFor(subjectId);
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
	public void addVote(final UUID subjectId, final UUID annotationId) {
		doUserAction(new AnnotationVoteAction(annotationId, subjectId));
	}

	@Override
	public void removeVote(final UUID subjectId, final UUID annotationId) {
		doUserAction(new AnnotationVoteRemoveAction(annotationId, subjectId));
	}

	@Override
	public void deprecateAnnotation(final UUID subjectId, final UUID annotationId) {
		doUserAction(new AnnotationDeprecateAction(subjectId, annotationId));
	}

	@Override
	public void removeDeprecation(final UUID subjectId, final UUID annotationId) {
		doUserAction(new AnnotationRemoveDeprecationAction(subjectId, annotationId));
	}

	@Override
	public void markAsImpediment(final UUID subjectId, final UUID annotationId) {
		doUserAction(new ImpedimentCreateAction(subjectId, annotationId));
	}

	@Override
	public void markAsSolveImpediment(final UUID subjectId, final UUID annotationId) {
		doUserAction(new ImpedimentSolveAction(subjectId, annotationId));
	}

	private void doUserAction(final ModelAction action) {
		actionExecutionService.onUserActionExecutionRequest(action);
	}

	private ActionExecutionListener getActionExecutionListener() {
		if (actionExecutionListener == null) actionExecutionListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context,
					final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet, final boolean isUserAction) {

				final UUID referenceId = action.getReferenceId();

				final boolean isCreation = action instanceof AnnotationCreateAction;
				if (isCreation || action instanceof AnnotationRemoveAction) {
					updateScopeTree(action, context);
					notifyAnnotationModification(referenceId, actionContext.getUserEmail(), isCreation);
				}
			}

			private void notifyAnnotationModification(final UUID referenceId, final String authorEmail, final boolean isCreation) {
				if (authorEmail.equals(authenticationService.getCurrentUser().getEmail())) return;

				final UUID projectId = getCurrentProjectId();
				for (final AnnotationModificationListener listener : annotationCreationListeners) {
					listener.onAnnotationModification(projectId, referenceId, authorEmail, isCreation);
				}
			}

			private void updateScopeTree(final ModelAction action, final ProjectContext context) {
				try {
					final Scope scope = context.findScope(action.getReferenceId());
					eventBus.fireEvent(new ScopeDetailChangeEvent(scope, hasDetails(scope.getId())));
				}
				catch (final ScopeNotFoundException e) {
					// It's not scope so don't need to update the view
				}
			}
		};
		return actionExecutionListener;
	}

	@Override
	public List<Annotation> getAnnotationsFor(final UUID subjectId) {
		return getCurrentContext().findAnnotationsFor(subjectId);
	}

	private ProjectContext getCurrentContext() {
		return contextProviderService.getCurrentProjectContext();
	}

	private UUID getCurrentProjectId() {
		return getCurrentContext().getProjectRepresentation().getId();
	}

	@Override
	public void addAnnotationModificationListener(final AnnotationModificationListener listener) {
		if (this.annotationCreationListeners.contains(listener)) return;

		this.annotationCreationListeners.add(listener);
	}

	@Override
	public void removeAnnotationModificationListener(final AnnotationModificationListener listener) {
		this.annotationCreationListeners.remove(listener);
	}

	public interface AnnotationModificationListener {

		void onAnnotationModification(UUID projectId, UUID subjectId, String authorEmail, boolean isCreation);

	}

}
