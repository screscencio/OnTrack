package br.com.oncast.ontrack.client.services.actionSync;

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

	private boolean active;
	private final RequestDispatchService requestDispatchService;

	public ActionSyncService(final RequestDispatchService requestDispatchService, final ServerPushClientService serverPushClientService,
			final ActionExecutionService actionExecutionService) {
		this.requestDispatchService = requestDispatchService;

		serverPushClientService.registerServerEventHandler(ServerActionSyncEvent.class, new ServerActionSyncEventHandler() {

			@Override
			public void onEvent(final ServerActionSyncEvent event) {
				final ModelAction action = event.getAction();
				try {
					actionExecutionService.executeNonUserAction(action);
				}
				catch (final UnableToCompleteActionException e) {
					threatSyncingError();
				}
			}
		});
		actionExecutionService.addActionExecutionListener(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (!active) return;
				if (!isUserAction) return;

				notifyClientActionExecutionToServer(action);
			}
		});
	}

	// TODO Review the necessity of this method, that was created only to make implicit when the service is active or not.
	public void setActive(final boolean active) {
		this.active = active;
	}

	private void notifyClientActionExecutionToServer(final ModelAction action) {
		// TODO Display 'loading' UI indicator.
		requestDispatchService.dispatch(new ModelActionSyncRequest(action), new DispatchCallback<Void>() {

			@Override
			public void onRequestCompletition(final Void response) {
				// TODO Hide 'loading' UI indicator.
			}

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Analyze refactoring this exception handling into a communication centralized exception handler.
				if (caught instanceof InvalidIncomingAction) {
					threatSyncingError();
				}
				else {
					// TODO Hide 'loading' UI indicator.
					// TODO +++Treat communication failure.
					// TODO +++Notify Error threatment service.
					caught.printStackTrace();
				}
			}
		});
	}

	private void threatSyncingError() {
		// TODO +++Delegate treatment to Error threatment service eliminating the need for this method.
		Window.alert("An error ocurred while syncing actions with the server: \nAn invalid action was found. \n\nThe application will be briethly reloaded and some of your lattest changes may be rollbacked.");
		Window.Location.reload();
	}
}
