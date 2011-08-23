package br.com.oncast.ontrack.client.services.actionSync;

import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.requestDispatch.DispatchCallback;
import br.com.oncast.ontrack.client.services.requestDispatch.RequestDispatchService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.shared.exceptions.business.InvalidIncomingAction;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEvent;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEventHandler;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

import com.google.gwt.user.client.Window;

public class ActionSyncService {

	private final RequestDispatchService requestDispatchService;
	// TODO Extract logic related to this set into methods (maybe into another class).
	private final Set<ModelActionSyncRequest> requestsSentFromThisClient = new HashSet<ModelActionSyncRequest>();

	public ActionSyncService(final RequestDispatchService requestDispatchService, final ServerPushClientService serverPushClientService,
			final ActionExecutionService actionExecutionService) {
		this.requestDispatchService = requestDispatchService;

		serverPushClientService.registerServerEventHandler(ServerActionSyncEvent.class, new ServerActionSyncEventHandler() {

			@Override
			public void onEvent(final ServerActionSyncEvent event) {
				processServerActionSyncEvent(actionExecutionService, event);
			}
		});
		actionExecutionService.addActionExecutionListener(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> scopeSet, final boolean isUserAction) {
				handleActionExecution(action, isUserAction);
			}
		});
	}

	private void processServerActionSyncEvent(final ActionExecutionService actionExecutionService, final ServerActionSyncEvent event) {
		final ModelActionSyncRequest modelActionSyncRequest = event.getModelActionSyncRequest();
		if (requestsSentFromThisClient.remove(modelActionSyncRequest)) return;

		try {
			actionExecutionService.executeNonUserAction(modelActionSyncRequest.getAction());
		}
		catch (final UnableToCompleteActionException e) {
			threatSyncingError("The application is out of sync with the server.\n\nIt will be briethly reloaded and some of your lattest changes may be rollbacked.");
		}
	}

	private void handleActionExecution(final ModelAction action, final boolean isUserAction) {
		if (!isUserAction) return;
		notifyClientActionExecutionToServer(action);
	}

	private void notifyClientActionExecutionToServer(final ModelAction action) {
		// TODO Display 'loading' UI indicator.

		final ModelActionSyncRequest modelActionSyncRequest = new ModelActionSyncRequest(action);
		requestsSentFromThisClient.add(modelActionSyncRequest);
		requestDispatchService.dispatch(modelActionSyncRequest, new DispatchCallback<Void>() {

			@Override
			public void onRequestCompletition(final Void response) {
				// TODO Hide 'loading' UI indicator.
			}

			@Override
			public void onFailure(final Throwable caught) {
				requestsSentFromThisClient.remove(modelActionSyncRequest);

				// TODO Analyze refactoring this exception handling into a communication centralized exception handler.
				if (caught instanceof InvalidIncomingAction) {
					threatSyncingError("The application is out of sync with the server.\nA conflict between multiple client's states was detected.\n\nIt will be briethly reloaded and some of your lattest changes may be rollbacked.");
				}
				else {
					// TODO Hide 'loading' UI indicator.
					// TODO +++Treat communication failure.
					// TODO +++Notify Error treatment service.
					threatSyncingError("The application server is unreachable.\nCheck your internet connection.\n\nThe application will be briethly reloaded");
					caught.printStackTrace();
				}
			}
		});
	}

	private void threatSyncingError(final String message) {
		// TODO +++Delegate treatment to Error threatment service eliminating the need for this method.
		Window.alert(message);
		Window.Location.reload();
	}
}
