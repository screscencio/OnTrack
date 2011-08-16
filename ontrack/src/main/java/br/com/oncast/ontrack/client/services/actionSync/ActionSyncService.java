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
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEvent;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEventHandler;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

import com.google.gwt.user.client.Window;

public class ActionSyncService {

	private boolean active;
	protected Set<UUID> receivedServerPushActions = new HashSet<UUID>();

	public ActionSyncService(final RequestDispatchService requestDispatchService, final ServerPushClientService serverPushClientService,
			final ActionExecutionService actionExecutionService) {

		serverPushClientService.registerServerEventHandler(createServerActionSyncEventHandler(actionExecutionService));
		actionExecutionService.addActionExecutionListener(createActionExecutionListener(requestDispatchService));
	}

	private ServerActionSyncEventHandler createServerActionSyncEventHandler(final ActionExecutionService actionExecutionService) {
		return new ServerActionSyncEventHandler() {

			@Override
			public void onEvent(final ServerActionSyncEvent event) {
				final ModelAction action = event.getAction();
				// FIXME This way for action sync is not correct.
				receivedServerPushActions.add(action.getReferenceId());

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
	}

	private ActionExecutionListener createActionExecutionListener(final RequestDispatchService requestDispatchService) {
		return new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet) {
				if (!active) return;

				// FIXME This way for action sync is not correct.
				if (receivedServerPushActions.contains(action.getReferenceId())) {
					receivedServerPushActions.remove(action.getReferenceId());
					return;
				}
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
	}

	// TODO Review the necessity of this method, that was created only to make implicit when the service is active or not.
	public void setActive(final boolean active) {
		this.active = active;
	}

}
