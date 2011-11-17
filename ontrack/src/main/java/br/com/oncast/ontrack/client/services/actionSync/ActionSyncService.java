package br.com.oncast.ontrack.client.services.actionSync;

import java.util.Set;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.errorHandling.ErrorTreatmentService;
import br.com.oncast.ontrack.client.services.identification.ClientIdentificationProvider;
import br.com.oncast.ontrack.client.services.requestDispatch.RequestDispatchService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEvent;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEventHandler;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

public class ActionSyncService {

	private final ClientIdentificationProvider clientIdentificationProvider;

	private final ActionQueuedDispatcher actionQueuedDispatcher;

	private final ActionExecutionService actionExecutionService;

	private final ErrorTreatmentService errorTreatmentService;

	private final ProjectRepresentationProvider projectRepresentationProvider;

	public ActionSyncService(final RequestDispatchService requestDispatchService, final ServerPushClientService serverPushClientService,
			final ActionExecutionService actionExecutionService, final ClientIdentificationProvider clientIdentificationProvider,
			final ProjectRepresentationProvider projectRepresentationProvider, final ErrorTreatmentService errorTreatmentService) {
		this.projectRepresentationProvider = projectRepresentationProvider;
		this.errorTreatmentService = errorTreatmentService;
		this.actionExecutionService = actionExecutionService;
		this.clientIdentificationProvider = clientIdentificationProvider;
		this.actionQueuedDispatcher = new ActionQueuedDispatcher(requestDispatchService, clientIdentificationProvider, projectRepresentationProvider,
				errorTreatmentService);

		serverPushClientService.registerServerEventHandler(ServerActionSyncEvent.class, new ServerActionSyncEventHandler() {

			@Override
			public void onEvent(final ServerActionSyncEvent event) {
				processServerActionSyncEvent(event);
			}
		});
		actionExecutionService.addActionExecutionListener(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> scopeSet, final boolean isUserAction) {
				handleActionExecution(action, isUserAction);
			}
		});
	}

	private void processServerActionSyncEvent(final ServerActionSyncEvent event) {
		final ModelActionSyncRequest modelActionSyncRequest = event.getModelActionSyncRequest();
		if (isRequestOriginatedByThisClient(modelActionSyncRequest)) return;
		if (!isRequestPertinentToCurrentProject(modelActionSyncRequest)) return;

		try {
			for (final ModelAction modelAction : modelActionSyncRequest.getActionList())
				actionExecutionService.onNonUserActionRequest(modelAction);
		}
		catch (final UnableToCompleteActionException e) {
			errorTreatmentService.treatFatalError(
					"The application is out of sync with the server.\n\nIt will be briethly reloaded and some of your lattest changes may be rollbacked.", e);
		}
	}

	private void handleActionExecution(final ModelAction action, final boolean isUserAction) {
		if (!isUserAction) return;
		actionQueuedDispatcher.dispatch(action);
	}

	private boolean isRequestOriginatedByThisClient(final ModelActionSyncRequest modelActionSyncRequest) {
		return modelActionSyncRequest.getClientId().equals(clientIdentificationProvider.getClientId());
	}

	private boolean isRequestPertinentToCurrentProject(final ModelActionSyncRequest modelActionSyncRequest) {
		return projectRepresentationProvider.getCurrentProjectRepresentation().getId() == modelActionSyncRequest.getRequestedProjectId();
	}
}
