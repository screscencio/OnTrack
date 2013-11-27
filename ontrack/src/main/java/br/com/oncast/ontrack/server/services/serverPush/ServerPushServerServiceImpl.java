package br.com.oncast.ontrack.server.services.serverPush;

import br.com.oncast.ontrack.server.services.serverPush.atmosphere.OntrackAtmospherePushServer;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

// TODO Should this service be asynchronous? (Run in another thread so that it does not affect this client´s thread)
public class ServerPushServerServiceImpl implements ServerPushServerService {

	private static final Logger LOGGER = Logger.getLogger(ServerPushServerServiceImpl.class);

	private final ServerPushApi serverPushServer;
	private final Set<ServerPushConnectionListener> serverPushConnectionListenerSet = new HashSet<ServerPushConnectionListener>();

	public ServerPushServerServiceImpl() {
		serverPushServer = new OntrackAtmospherePushServer();

		this.serverPushServer.setConnectionListener(new InternalConnectionListener() {

			@Override
			public void onClientConnected(final ServerPushConnection connection) {
				for (final ServerPushConnectionListener listener : serverPushConnectionListenerSet)
					listener.onClientConnected(connection);
			}

			@Override
			public void onClientDisconnected(final ServerPushConnection connection) {
				for (final ServerPushConnectionListener listener : serverPushConnectionListenerSet)
					listener.onClientDisconnected(connection);
			}
		});
	}

	@Override
	public void pushEvent(final ServerPushEvent serverPushEvent, final Set<ServerPushConnection> clientSet) {
		for (final ServerPushConnection connection : clientSet) {
			LOGGER.debug("Pushing event (" + serverPushEvent.getClass().getSimpleName() + ") to client '" + connection + "'.");
			if (connection != null) serverPushServer.pushEvent(serverPushEvent, connection);
		}
	}

	@Override
	public void registerConnectionListener(final ServerPushConnectionListener listener) {
		serverPushConnectionListenerSet.add(listener);
	}
}
