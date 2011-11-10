package br.com.oncast.ontrack.server.services.actionBroadcast;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnection;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnectionListener;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEvent;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

// TODO Analyze the possibility of merging this class with ServerPushServerServiceImpl.
// TODO Analyze removing the connection control of ServerPushClients.
public class ActionBroadcastServiceImpl implements ActionBroadcastService {

	private static final Logger LOGGER = Logger.getLogger(ActionBroadcastServiceImpl.class);
	private final ServerPushServerService serverPushServerService;
	private final Set<ServerPushConnection> connectionSet = new HashSet<ServerPushConnection>();

	public ActionBroadcastServiceImpl(final ServerPushServerService serverPushServerService) {
		this.serverPushServerService = serverPushServerService;
		this.serverPushServerService.registerConnectionListener(new ServerPushConnectionListener() {

			@Override
			public void onClientConnected(final ServerPushConnection connection) {
				LOGGER.debug("Putting a new client into the map.");
				connectionSet.add(connection);
			}

			@Override
			public void onClientDisconnected(final ServerPushConnection connection) {
				LOGGER.debug("Removing a client from map.");
				connectionSet.remove(connection);
			}
		});
	}

	@Override
	public void broadcast(final ModelActionSyncRequest modelActionSyncRequest) {
		LOGGER.debug("Broadcasting " + ModelAction.class.getSimpleName() + " to '" + connectionSet.toArray().toString() + "'.");
		serverPushServerService.pushEvent(new ServerActionSyncEvent(modelActionSyncRequest), connectionSet);
	}

}
