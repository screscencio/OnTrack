package br.com.oncast.ontrack.server.services.serverPush;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import net.zschech.gwt.comet.server.CometServlet;
import net.zschech.gwt.comet.server.CometServletResponse;
import net.zschech.gwt.comet.server.CometSession;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.httpSessionProvider.HttpSessionProvider;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

/**
 * This class should have all of its properties static, because it may have multiple instances.
 */
public class GwtCometServlet extends CometServlet implements ServerPushApi {

	private static final Logger LOGGER = Logger.getLogger(GwtCometServlet.class);
	private static final long serialVersionUID = 1L;

	private static final Map<String, CometSession> cometSessionMap = new HashMap<String, CometSession>();
	private static ServerPushConnectionListener serverPushConnectionListener;

	public GwtCometServlet() {}

	@Override
	public void setServerPushConnectionListener(final ServerPushConnectionListener serverPushConnectionListener) {
		GwtCometServlet.serverPushConnectionListener = serverPushConnectionListener;
	}

	@Override
	public void pushEvent(final ServerPushEvent serverPushEvent, final GwtCometClientConnection client) {
		try {
			cometSessionMap.get(client.getSessionId()).enqueue(serverPushEvent);
		}
		catch (final IllegalStateException e) {
			// Purposefully ignored exception.
			// TODO Remove this print stack trace.
			e.printStackTrace();
			// TODO Analyze if removing a comet session would be necessary here. This exception is thrown exclusively when the client is disconnected.
		}
	}

	@Override
	public ServerPushConnection getCurrentClient(final HttpSessionProvider httpSessionProvider) throws ServerPushException {
		final String sessionId = httpSessionProvider.getCurrentSession().getId();
		if (!cometSessionMap.containsKey(sessionId)) throw new ServerPushException("The current session is not mapped to any connected client.");
		return new GwtCometClientConnection(sessionId);
	}

	private static void addCometSession(final CometSession cometSession) {
		LOGGER.debug("A new commet session was added.");
		final String sessionId = cometSession.getHttpSession().getId();

		cometSessionMap.put(sessionId, cometSession);
		if (serverPushConnectionListener != null) serverPushConnectionListener.onClientConnected(new GwtCometClientConnection(sessionId));
	}

	private static void removeCometSession(final String sessionId) {
		LOGGER.debug("A new commet session was removed.");

		cometSessionMap.remove(sessionId);
		if (serverPushConnectionListener != null) serverPushConnectionListener.onClientDisconnected(new GwtCometClientConnection(sessionId));
	}

	@Override
	protected void doComet(final CometServletResponse cometResponse) throws ServletException, IOException {
		final CometSession cometSession = cometResponse.getSession(false);
		if (cometSession == null || !cometSession.isValid()) addCometSession(cometResponse.getSession(true));
	}

	public static class GwtCometServerHttpSessionListener implements HttpSessionListener {

		@Override
		public void sessionCreated(final HttpSessionEvent event) {}

		@Override
		public void sessionDestroyed(final HttpSessionEvent event) {
			final HttpSession httpSession = event.getSession();
			final CometSession cometSession = CometServlet.getCometSession(httpSession, false);
			if (cometSession != null) cometSession.invalidate();

			final String httpSessionId = httpSession.getId();
			GwtCometServlet.removeCometSession(httpSessionId);
		}
	}
}
