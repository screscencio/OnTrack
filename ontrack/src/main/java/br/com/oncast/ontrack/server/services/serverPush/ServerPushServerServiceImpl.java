package br.com.oncast.ontrack.server.services.serverPush;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

// TODO Should this service be asynchronous? (Run in another thread so that it does not affect this client´s thread)
public class ServerPushServerServiceImpl implements ServerPushServerService {

	private static final Logger LOGGER = Logger.getLogger(ServerPushServerServiceImpl.class);

	private final ServerPushApi serverPushServer;
	private final Set<ServerPushConnectionListener> serverPushConnectionListenerSet = new HashSet<ServerPushConnectionListener>();

	// TODO Should this be on the ServerPushServerService "user"?
	private final Map<UUID, ServerPushConnection> clientConnectionMap;

	public ServerPushServerServiceImpl() {
		this.serverPushServer = new GwtCometServlet();
		this.clientConnectionMap = new HashMap<UUID, ServerPushConnection>();
		this.serverPushServer.setConnectionListener(new InternalConnectionListener() {

			@Override
			public void onClientConnected(final ServerPushConnection connection) {
				final UUID clientId = new UUID(connection.getClientId());

				clientConnectionMap.put(clientId, connection);
				for (final ServerPushConnectionListener listener : serverPushConnectionListenerSet)
					listener.onClientConnected(clientId);
			}

			@Override
			public void onClientDisconnected(final ServerPushConnection connection) {
				final UUID clientId = new UUID(connection.getClientId());

				clientConnectionMap.remove(clientId);
				for (final ServerPushConnectionListener listener : serverPushConnectionListenerSet)
					listener.onClientDisconnected(clientId);
			}
		});
	}

	@Override
	public void pushEvent(final ServerPushEvent serverPushEvent, final Set<UUID> clientSet) {
		for (final UUID clientId : clientSet) {
			LOGGER.debug("Pushing event (" + serverPushEvent.getClass().getSimpleName() + ") to client '" + clientId + "'.");

			final ServerPushConnection connection = clientConnectionMap.get(clientId);
			if (connection != null) serverPushServer.pushEvent(serverPushEvent, (GwtCometClientConnection) connection);
		}
	}

	@Override
	public void registerConnectionListener(final ServerPushConnectionListener listener) {
		serverPushConnectionListenerSet.add(listener);
	}

}
