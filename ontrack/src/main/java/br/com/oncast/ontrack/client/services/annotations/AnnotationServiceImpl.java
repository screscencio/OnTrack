package br.com.oncast.ontrack.client.services.annotations;

import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.ui.components.releasepanel.events.ReleaseDetailUpdateEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeDetailUpdateEvent;
import br.com.oncast.ontrack.client.ui.places.details.DetailPlace;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationDeprecateAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationRemoveDeprecationAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentCreateAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentSolveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.web.bindery.event.shared.EventBus;

public class AnnotationServiceImpl implements AnnotationService {

	private final ActionExecutionService actionExecutionService;
	private final ContextProviderService contextProviderService;
	private final ApplicationPlaceController applicationPlaceController;
	private ActionExecutionListener actionExecutionListener;
	private final EventBus eventBus;

	public AnnotationServiceImpl(final ActionExecutionService actionExecutionService, final ContextProviderService contextProviderService,
			final ApplicationPlaceController applicationPlaceController, final EventBus eventBus) {
		this.actionExecutionService = actionExecutionService;
		this.contextProviderService = contextProviderService;
		this.applicationPlaceController = applicationPlaceController;
		this.eventBus = eventBus;

		actionExecutionService.addActionExecutionListener(getActionExecutionListener());
	}

	@Override
	public boolean hasDetails(final UUID subjectId) {
		final ProjectContext context = contextProviderService.getCurrentProjectContext();

		return context.hasChecklistsFor(subjectId) || hasAnnotationsFor(subjectId);
	}

	private boolean hasAnnotationsFor(final UUID subjectId) {
		return hasMatchingAnnotation(subjectId, AnnotationType.SIMPLE, false);
	}

	@Override
	public void showAnnotationsFor(final UUID subjectId) {
		applicationPlaceController.goTo(new DetailPlace(getCurrentProjectId(), subjectId, applicationPlaceController.getCurrentPlace(), true));
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

				if (action instanceof AnnotationAction || action instanceof ImpedimentAction || action instanceof ChecklistAction) {
					fireScopeDetailUpdateEvent(action, context);
				}
			}

			private void fireScopeDetailUpdateEvent(final ModelAction action, final ProjectContext context) {
				try {
					final Scope scope = context.findScope(action.getReferenceId());
					eventBus.fireEvent(new ScopeDetailUpdateEvent(scope, hasDetails(scope.getId()), hasOpenImpediment(scope.getId())));
				}
				catch (final ScopeNotFoundException e) {
					try {
						final Release release = context.findRelease(action.getReferenceId());
						eventBus.fireEvent(new ReleaseDetailUpdateEvent(release, hasDetails(release.getId()), hasOpenImpediment(release.getId())));
					}
					catch (final ReleaseNotFoundException ex) {
						// It's not scope nor release so don't need to update the view
					}
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
	public boolean hasOpenImpediment(final UUID subjectId) {
		return hasMatchingAnnotation(subjectId, AnnotationType.OPEN_IMPEDIMENT, false);
	}

	private boolean hasMatchingAnnotation(final UUID subjectId, final AnnotationType type, final boolean isDeprecated) {
		for (final Annotation annotation : getCurrentContext().findAnnotationsFor(subjectId)) {
			if (isDeprecated == annotation.isDeprecated() && type.equals(annotation.getType())) return true;
		}
		return false;
	}

}
