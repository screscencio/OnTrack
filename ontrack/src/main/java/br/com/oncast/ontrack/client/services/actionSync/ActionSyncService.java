package br.com.oncast.ontrack.client.services.actionSync;

import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.alerting.AlertConfirmationListener;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionSync.ModelActionSyncEvent;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public class ActionSyncService {

	private final ClientErrorMessages messages = GWT.create(ClientErrorMessages.class);

	private final ActionQueuedDispatcher actionQueuedDispatcher;

	private final ActionExecutionService actionExecutionService;

	private final ClientAlertingService alertingService;

	private final ProjectRepresentationProvider projectRepresentationProvider;

	public ActionSyncService(final DispatchService requestDispatchService, final ServerPushClientService serverPushClientService,
			final ActionExecutionService actionExecutionService, final ProjectRepresentationProvider projectRepresentationProvider,
			final ClientAlertingService alertingService) {
		this.projectRepresentationProvider = projectRepresentationProvider;
		this.actionExecutionService = actionExecutionService;
		this.alertingService = alertingService;
		this.actionQueuedDispatcher = new ActionQueuedDispatcher(requestDispatchService, projectRepresentationProvider, alertingService, messages);

		serverPushClientService.registerServerEventHandler(ModelActionSyncEvent.class, new ServerActionSyncEventHandler() {

			@Override
			public void onEvent(final ModelActionSyncEvent event) {
				processServerActionSyncEvent(event);
			}
		});
		this.actionExecutionService.addActionExecutionListener(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext, final Set<UUID> scopeSet,
					final boolean isUserAction) {
				handleActionExecution(action, isUserAction);
			}
		});
	}

	private void processServerActionSyncEvent(final ModelActionSyncEvent event) {
		checkIfRequestIsPertinentToCurrentProject(event);

		try {
			final ActionContext actionContext = event.getActionContext();
			for (final ModelAction modelAction : event.getActionList()) {
				actionExecutionService.onNonUserActionRequest(modelAction, actionContext);
			}
		}
		catch (final UnableToCompleteActionException e) {
			alertingService.showErrorWithConfirmation(messages.someChangesConflicted(),
					new AlertConfirmationListener() {
						@Override
						public void onConfirmation() {
							Window.Location.reload();
						}
					});
		}
	}

	private void handleActionExecution(final ModelAction action, final boolean isUserAction) {
		if (!isUserAction) return;
		actionQueuedDispatcher.dispatch(action);
	}

	private void checkIfRequestIsPertinentToCurrentProject(final ModelActionSyncEvent event) {
		final UUID requestedProjectId = event.getProjectId();
		final UUID currentProjectId = projectRepresentationProvider.getCurrent().getId();
		if (!requestedProjectId.equals(currentProjectId)) throw new RuntimeException(
				"This client received an action for project '" + requestedProjectId + "' but it is currently on project '" + currentProjectId
						+ "'. Please notify OnTrack team.");
	}
}
