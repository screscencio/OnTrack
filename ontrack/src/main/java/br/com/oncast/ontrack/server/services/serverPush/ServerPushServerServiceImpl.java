package br.com.oncast.ontrack.server.services.serverPush;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import net.zschech.gwt.comet.server.CometServlet;
import net.zschech.gwt.comet.server.CometSession;
import br.com.oncast.ontrack.server.services.httpSessionProvider.HttpSessionProvider;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

// FIXME Should this service be asynchronous? (Run in another thread so that it does not affect this client´s thread)
public class ServerPushServerServiceImpl implements ServerPushServerService {

	private final static Map<String, CometSession> cometSessionMap = new HashMap<String, CometSession>();
	private final HttpSessionProvider httpSessionProvider;
	private static Set<ServerPushConnectionListener> serverPushConnectionListenerSet = new HashSet<ServerPushConnectionListener>();

	public ServerPushServerServiceImpl(final HttpSessionProvider httpSessionProvider) {
		this.httpSessionProvider = httpSessionProvider;
		ServerPushServlet.addCometSessionListener(new CometSessionListener() {
			@Override
			public void onSessionCreated(final CometSession cometSession) {
				addCometSession(cometSession);
			}

			@Override
			public void onSessionDestroyed(final String sessionId) {
				removeCometSession(sessionId);
			}
		});
	}

	@Override
	public void pushEvent(final ServerPushEvent serverPushEvent, final Collection<ServerPushClient> clientList) {
		// TODO Remove syso
		System.out.println("Sending server push event to " + clientList.size() + " clients.");

		for (final ServerPushClient client : clientList) {
			System.out.println("Client: " + client.getSessionId());
			try {
				cometSessionMap.get(client.getSessionId()).enqueue(serverPushEvent);
			}
			catch (final IllegalStateException e) {
				// This exception is thrown by CometSession when trying to send a message through an invalid connection and by java.util.Queue if the stack is
				// full.
				removeCometSession(client.getSessionId());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void registerConnectionListener(final ServerPushConnectionListener listener) {
		serverPushConnectionListenerSet.add(listener);
	}

	/**
	 * @see br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService#getSender()
	 */
	@Override
	public ServerPushClient getSender() {
		return new ServerPushClient(httpSessionProvider.getCurrentSession().getId());
	}

	private void addCometSession(final CometSession cometSession) {
		final String sessionId = cometSession.getHttpSession().getId();
		cometSessionMap.put(sessionId, cometSession);
		System.out.println("A new comet session was put inside the map: " + sessionId);

		notifyListenersOnClientConnected(sessionId);
	}

	protected void removeCometSession(final String sessionId) {
		removeCometConnectionFromMap(sessionId);
		notifyListenersOnClientDisconnected(sessionId);
	}

	private static void notifyListenersOnClientConnected(final String clientId) {
		// TODO Remove syso
		System.out.println("Notifying listeners on new client connection. Client id: " + clientId);
		for (final ServerPushConnectionListener listener : serverPushConnectionListenerSet) {
			listener.onClientConnected(new ServerPushClient(clientId));
		}
	}

	private static void notifyListenersOnClientDisconnected(final String clientId) {
		// TODO Remove syso
		System.out.println("Notifying listeners on client disconnection. Client id: " + clientId);
		for (final ServerPushConnectionListener listener : serverPushConnectionListenerSet) {
			listener.onClientDisconnected(new ServerPushClient(clientId));
		}
	}

	private static void removeCometConnectionFromMap(final String id) {
		// TODO Remove syso
		System.out.println("Removing comet session from server push channel...");
		cometSessionMap.remove(id);
	}

	/**
	 * Listens when a {@link HttpSession} is created and destroyed, creates a {@link CometSession} (on session creation) and notifies listeners.
	 */
	public static class CometHttpSessionListener implements HttpSessionListener {
		@Override
		public void sessionCreated(final HttpSessionEvent event) {}

		@Override
		public void sessionDestroyed(final HttpSessionEvent event) {
			// TODO Remove syso
			System.out.println("HttpSessionListener#sessionDestroyed.");

			// FIXME
			final HttpSession httpSession = event.getSession();
			final CometSession cometSession = CometServlet.getCometSession(httpSession, false);
			if (cometSession != null) cometSession.invalidate();

			final String httpSessionId = httpSession.getId();
			removeCometConnectionFromMap(httpSessionId);
			notifyListenersOnClientDisconnected(httpSessionId);
		}
	}

}
