package br.com.oncast.ontrack.client.services.annotations;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.notification.ClientNotificationService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.ui.places.details.DetailPlace;
import br.com.oncast.ontrack.shared.model.action.AnnotationAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationRemoveAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteRemoveAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.exceptions.AnnotationNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class AnnotationServiceImpl implements AnnotationService {

	private final ActionExecutionService actionExecutionService;
	private final ContextProviderService contextProviderService;
	private final AuthenticationService authenticationService;
	private final ClientNotificationService clientNotificationService;
	private final ApplicationPlaceController applicationPlaceController;

	public AnnotationServiceImpl(final ActionExecutionService actionExecutionService, final ContextProviderService contextProviderService,
			final AuthenticationService authenticationService, final ClientNotificationService clientNotificationService,
			final ApplicationPlaceController applicationPlaceController) {
		this.actionExecutionService = actionExecutionService;
		this.contextProviderService = contextProviderService;
		this.authenticationService = authenticationService;
		this.clientNotificationService = clientNotificationService;
		this.applicationPlaceController = applicationPlaceController;
	}

	@Override
	public boolean hasDetails(final UUID subjectId) {
		final ProjectContext context = contextProviderService.getCurrentProjectContext();
		return context.hasChecklistsFor(subjectId) || context.hasAnnotationsFor(subjectId);
	}

	@Override
	public void showAnnotationsFor(final UUID subjectId) {
		final UUID projectId = contextProviderService.getCurrentProjectContext().getProjectRepresentation().getId();
		applicationPlaceController.goTo(new DetailPlace(projectId, subjectId, applicationPlaceController.getCurrentPlace()));
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
		return annotation.hasVoted(authenticationService.getCurrentUser().getEmail());
	}

	private void doUserAction(final AnnotationAction action) {
		actionExecutionService.onUserActionExecutionRequest(action);
	}

}
