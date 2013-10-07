package br.com.oncast.ontrack.client.services.details;

import java.util.List;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.ui.components.releasepanel.events.ReleaseDetailUpdateEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeDetailUpdateEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.SubjectDetailUpdateEvent;
import br.com.oncast.ontrack.client.ui.places.details.DetailPlace;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationDeprecateAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationRemoveAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationRemoveDeprecationAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistAction;
import br.com.oncast.ontrack.shared.model.action.ChecklistItemAction;
import br.com.oncast.ontrack.shared.model.action.DescriptionAction;
import br.com.oncast.ontrack.shared.model.action.DescriptionCreateAction;
import br.com.oncast.ontrack.shared.model.action.DescriptionRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentCreateAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentSolveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.description.exceptions.DescriptionNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;

public class DetailServiceImpl implements DetailService {

	private final ActionExecutionService actionExecutionService;
	private final ContextProviderService contextProviderService;
	private final ApplicationPlaceController applicationPlaceController;
	private ActionExecutionListener actionExecutionListener;
	private final EventBus eventBus;

	public DetailServiceImpl(final ActionExecutionService actionExecutionService, final ContextProviderService contextProviderService,
			final ApplicationPlaceController applicationPlaceController, final EventBus eventBus) {
		this.actionExecutionService = actionExecutionService;
		this.contextProviderService = contextProviderService;
		this.applicationPlaceController = applicationPlaceController;
		this.eventBus = eventBus;

		actionExecutionService.addActionExecutionListener(getActionExecutionListener());
	}

	@Override
	public boolean hasDetails(final UUID subjectId) {
		final ProjectContext context = contextProviderService.getCurrent();
		return context.hasChecklistsFor(subjectId) || context.hasDescriptionFor(subjectId) || hasAnnotationsFor(subjectId);

	}

	private boolean hasAnnotationsFor(final UUID subjectId) {
		return hasMatchingAnnotation(subjectId, AnnotationType.SIMPLE, false);
	}

	@Override
	public void showDetailsFor(final UUID subjectId) {
		applicationPlaceController.goTo(new DetailPlace(getCurrentProjectId(), subjectId, applicationPlaceController.getCurrentPlace(), true));
	}

	@Override
	public void createAnnotationFor(final UUID subjectId, final String message, final UUID attachmentId) {
		doUserAction(new AnnotationCreateAction(subjectId, message, attachmentId));
	}

	@Override
	public void removeAnnotation(final UUID subjectId, final UUID annotationId) {
		doUserAction(new AnnotationRemoveAction(subjectId, annotationId));
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
					final ActionExecutionContext executionContext, final boolean isUserAction) {

				if (action instanceof ChecklistItemAction) fireSubjectDetailUpdateEvent(((ChecklistItemAction) action).getSubjectId(), context);

				else if (action instanceof AnnotationAction
						|| action instanceof ImpedimentAction
						|| action instanceof ChecklistAction
						|| action instanceof DescriptionAction
				) fireSubjectDetailUpdateEvent(action.getReferenceId(), context);
			}

			private void fireSubjectDetailUpdateEvent(final UUID subjectId, final ProjectContext context) {
				final SubjectDetailUpdateEvent event = getDetailUpdateEvent(subjectId);
				if (event != null) eventBus.fireEvent((Event<?>) event);
			}

		};
		return actionExecutionListener;
	}

	@Override
	public List<Annotation> getAnnotationsFor(final UUID subjectId) {
		return getCurrentContext().findAnnotationsFor(subjectId);
	}

	@Override
	public List<Annotation> getImpedimentsFor(final UUID subjectId) {
		return getCurrentContext().findImpedimentsFor(subjectId);
	}

	private ProjectContext getCurrentContext() {
		return contextProviderService.getCurrent();
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
			if (isDeprecated == annotation.isDeprecated() && type == annotation.getType()) return true;
		}
		return false;
	}

	@Override
	public SubjectDetailUpdateEvent getDetailUpdateEvent(final UUID subjectId) {
		final ProjectContext context = contextProviderService.getCurrent();
		SubjectDetailUpdateEvent event = null;
		try {
			final Scope scope = context.findScope(subjectId);
			event = addDetails(new ScopeDetailUpdateEvent(scope), context);
		}
		catch (final ScopeNotFoundException e) {
			try {
				final Release release = context.findRelease(subjectId);
				event = addDetails(new ReleaseDetailUpdateEvent(release), context);
			}
			catch (final ReleaseNotFoundException ex) {
				// It's not scope nor release so don't need to update the view
			}
		}
		return event;
	}

	private SubjectDetailUpdateEvent addDetails(final SubjectDetailUpdateEvent event, final ProjectContext context) {
		final UUID subjectId = event.getSubjectId();

		event.setChecklists(context.findChecklistsFor(subjectId));
		event.setAnnotations(context.findAnnotationsFor(subjectId));

		try {
			event.setDescription(context.findDescriptionFor(subjectId));
		}
		catch (final DescriptionNotFoundException e) {}
		return event;
	}

	@Override
	public void updateDescription(final UUID subjectId, final String text) {
		if (text.trim().isEmpty()) {
			try {
				final Description description = findDescriptionFor(subjectId);
				actionExecutionService.onUserActionExecutionRequest(new DescriptionRemoveAction(subjectId, description.getId(), true));
			}
			catch (final DescriptionNotFoundException e) {}
		}
		else actionExecutionService.onUserActionExecutionRequest(new DescriptionCreateAction(subjectId, text));
	}

	private Description findDescriptionFor(final UUID subjectId) throws DescriptionNotFoundException {
		return getCurrentContext().findDescriptionFor(subjectId);
	}

}
