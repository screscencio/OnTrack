package br.com.oncast.ontrack.server.services.actionSync;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnectionListener;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushException;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnection;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEvent;

public class ActionBroadcastServiceImpl implements ActionBroadcastService {

	private static final Logger LOGGER = Logger.getLogger(ActionBroadcastServiceImpl.class);
	private final ServerPushServerService serverPushServerService;
	protected Set<ServerPushConnection> connectionSet = new HashSet<ServerPushConnection>();

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
	public void broadcast(final ModelAction action) {
		final Set<ServerPushConnection> destinationClientSet = getBroadcastDestinationConnections();
		LOGGER.debug("Broadcasting " + ModelAction.class.getSimpleName() + " to '" + destinationClientSet.toArray().toString() + "'.");
		serverPushServerService.pushEvent(new ServerActionSyncEvent(action), destinationClientSet);
	}

	private Set<ServerPushConnection> getBroadcastDestinationConnections() {
		final Set<ServerPushConnection> returnSet = new HashSet<ServerPushConnection>(connectionSet);
		try {
			final ServerPushConnection currentClient = serverPushServerService.getCurrentClient();
			returnSet.remove(currentClient);
		}
		catch (final ServerPushException e) {
			// Purposefully ignored exception. Ignoring this all registered clients will receive the broadcast.
			LOGGER.error("The client that originated the action is not registered in the ServerPush service.", e);
		}
		return returnSet;
	}
}
