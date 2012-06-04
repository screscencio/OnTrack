package br.com.oncast.ontrack.client.services.actionSync;

import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.notification.ClientNotificationService;
import br.com.oncast.ontrack.client.services.notification.NotificationConfirmationListener;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEvent;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEventHandler;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

import com.google.gwt.user.client.Window;

public class ActionSyncService {

	private final ActionQueuedDispatcher actionQueuedDispatcher;

	private final ActionExecutionService actionExecutionService;

	private final ClientNotificationService notificationService;

	private final ProjectRepresentationProvider projectRepresentationProvider;

	public ActionSyncService(final DispatchService requestDispatchService, final ServerPushClientService serverPushClientService,
			final ActionExecutionService actionExecutionService, final ProjectRepresentationProvider projectRepresentationProvider,
			final ClientNotificationService notificationService) {
		this.projectRepresentationProvider = projectRepresentationProvider;
		this.actionExecutionService = actionExecutionService;
		this.notificationService = notificationService;
		this.actionQueuedDispatcher = new ActionQueuedDispatcher(requestDispatchService, projectRepresentationProvider, notificationService);

		serverPushClientService.registerServerEventHandler(ServerActionSyncEvent.class, new ServerActionSyncEventHandler() {

			@Override
			public void onEvent(final ServerActionSyncEvent event) {
				processServerActionSyncEvent(event);
			}
		});
		this.actionExecutionService.addActionExecutionListener(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> scopeSet, final boolean isUserAction) {
				handleActionExecution(action, isUserAction);
			}
		});
	}

	private void processServerActionSyncEvent(final ServerActionSyncEvent event) {
		final ModelActionSyncRequest modelActionSyncRequest = event.getModelActionSyncRequest();

		checkIfRequestIsPertinentToCurrentProject(modelActionSyncRequest);

		try {
			final ActionContext actionContext = modelActionSyncRequest.getActionContext();
			for (final ModelAction modelAction : modelActionSyncRequest.getActionList()) {
				actionExecutionService.onNonUserActionRequest(modelAction, actionContext);
			}
		}
		catch (final UnableToCompleteActionException e) {
			notificationService.showErrorWithConfirmation("Some of the lattest changes conflicted.",
					new NotificationConfirmationListener() {
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

	private void checkIfRequestIsPertinentToCurrentProject(final ModelActionSyncRequest modelActionSyncRequest) {
		final long requestedProjectId = modelActionSyncRequest.getProjectId();
		final long currentProjectId = projectRepresentationProvider.getCurrent().getId();
		if (requestedProjectId != currentProjectId) throw new RuntimeException(
				"This client received an action for project '" + requestedProjectId + "' but it is currently on project '" + currentProjectId
						+ "'. Please notify OnTrack team.");
	}
}
