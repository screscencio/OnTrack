package br.com.oncast.ontrack.client.services.annotations;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.notification.ClientNotificationService;
import br.com.oncast.ontrack.client.ui.components.annotations.AnnotationsPanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupCloseListener;
import br.com.oncast.ontrack.shared.model.action.AnnotationAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationRemoveAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationVoteRemoveAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.exceptions.AnnotationNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class AnnotationServiceImpl implements AnnotationService {

	private final ActionExecutionService actionExecutionService;
	private final ContextProviderService contextProviderService;
	private final AuthenticationService authenticationService;
	private final ClientNotificationService clientNotificationService;

	public AnnotationServiceImpl(final ActionExecutionService actionExecutionService, final ContextProviderService contextProviderService,
			final AuthenticationService authenticationService, final ClientNotificationService clientNotificationService) {
		this.actionExecutionService = actionExecutionService;
		this.contextProviderService = contextProviderService;
		this.authenticationService = authenticationService;
		this.clientNotificationService = clientNotificationService;
	}

	@Override
	public boolean hasDetails(final UUID subjectId) {
		final ProjectContext context = contextProviderService.getCurrentProjectContext();
		return context.hasChecklistsFor(subjectId) || context.hasAnnotationsFor(subjectId);
	}

	@Override
	public void showAnnotationsFor(final Scope scope, final PopupCloseListener closeListener) {
		final AnnotationsPanel panel = AnnotationsPanel.forScope(scope);

		PopupConfig.configPopup().popup(panel).onClose(closeListener).setModal(true).pop();
	}

	@Override
	public void showAnnotationsFor(final Scope scope) {
		showAnnotationsFor(scope, null);
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
	public void showAnnotationsFor(final Release release) {
		final AnnotationsPanel panel = AnnotationsPanel.forRelease(release);

		PopupConfig.configPopup().popup(panel).setModal(true).pop();
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
