package br.com.oncast.ontrack.client.services.actionSync;

import java.util.Set;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.communication.requestDispatch.DispatchCallback;
import br.com.oncast.ontrack.client.services.communication.requestDispatch.RequestDispatchService;
import br.com.oncast.ontrack.client.services.communication.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.shared.exceptions.business.InvalidIncomingAction;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEvent;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEventHandler;
import br.com.oncast.ontrack.shared.services.communication.requestDispatch.ModelActionSyncRequest;

import com.google.gwt.user.client.Window;

public class ActionSyncService {

	private boolean active;

	public ActionSyncService(final RequestDispatchService requestDispatchService, final ServerPushClientService serverPushClientService,
			final ActionExecutionService actionExecutionService) {
		final ServerActionSyncEventHandler serverActionSyncEventHandler = new ServerActionSyncEventHandler() {

			@Override
			public void onEvent(final ServerActionSyncEvent event) {
				final ModelAction action = event.getAction();
				// FIXME Should add action to sync verification stack, and as actions come from onActionExecution verify their presence in the stack (removing
				// them).
				// FIXME Use a method that does not add anything to the client stack.
				actionExecutionService.onActionExecutionRequest(action);
				// FIXME Threat possible errors and then threat/warn synching error (this method should throw UnableToCompleteActionException
			}

			@Override
			// TODO Should this method be incorporated to the interface (making it an abstract class) using generics?
			public Class<ServerActionSyncEvent> getHandledEventClass() {
				return ServerActionSyncEvent.class;
			}
		};
		serverPushClientService.registerServerEventHandler(serverActionSyncEventHandler);

		final ActionExecutionListener actionExecutionListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet) {
				if (!active) return;

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
							Window.alert("An error ocurred while syncing actions with the server: \nAn invalid action was found. \n\nThe application will be briethly reloaded and some of your lattest changes may be rollbacked.");
							Window.Location.reload();
						}
						else {
							// TODO Hide 'loading' UI indicator.
							// TODO +++Treat communication failure.
							caught.printStackTrace();
						}
					}
				});
			}
		};
		actionExecutionService.addActionExecutionListener(actionExecutionListener);
	}

	// TODO Review the necessity of this method, that was created only to make implicit when the service is active or not.
	public void setActive(final boolean active) {
		this.active = active;
	}

}
