package br.com.oncast.ontrack.server.services.serverPush;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

// TODO Should this service be asynchronous? (Run in another thread so that it does not affect this client´s thread)
public class ServerPushServerServiceImpl implements ServerPushServerService {

	private static final Logger LOGGER = Logger.getLogger(ServerPushServerServiceImpl.class);

	private final ServerPushApi serverPushServer;
	private final Set<ServerPushConnectionListener> serverPushConnectionListenerSet = new HashSet<ServerPushConnectionListener>();

	public ServerPushServerServiceImpl() {
		this.serverPushServer = new GwtCometServlet();
		this.serverPushServer.setServerPushConnectionListener(new ServerPushConnectionListener() {

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
		for (final ServerPushConnection client : clientSet) {
			LOGGER.debug("Pushing event (" + serverPushEvent.getClass().getSimpleName() + ") to client '" + client + "'.");
			serverPushServer.pushEvent(serverPushEvent, (GwtCometClientConnection) client);
		}
	}

	@Override
	public void registerConnectionListener(final ServerPushConnectionListener listener) {
		serverPushConnectionListenerSet.add(listener);
	}

}
