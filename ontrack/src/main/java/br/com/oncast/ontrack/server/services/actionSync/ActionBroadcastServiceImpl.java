package br.com.oncast.ontrack.server.services.actionSync;

import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEvent;

public class ActionBroadcastServiceImpl implements ActionBroadcastService {

	private final ServerPushServerService serverPushServerService;

	public ActionBroadcastServiceImpl(final ServerPushServerService serverPushServerService) {
		this.serverPushServerService = serverPushServerService;
	}

	@Override
	public void broadcast(final ModelAction action) {
		// FIXME Send action only to the correct clients, maybe accessing an AuthenticationService in order to map clients. The abstraction should provide
		// enought flexibility to decide clients within the correct project and at the same time not send actions to the client that originated the action.
		// In short, IT SHOULD MANAGE CLIENTS AND ITS LOGICAL GROUPINGS.
		serverPushServerService.pushEvent(new ServerActionSyncEvent(action));
	}
}
