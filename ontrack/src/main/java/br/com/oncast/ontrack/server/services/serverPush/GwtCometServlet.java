package br.com.oncast.ontrack.server.services.serverPush;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import net.zschech.gwt.comet.server.CometServlet;
import net.zschech.gwt.comet.server.CometServletResponse;
import net.zschech.gwt.comet.server.CometSession;

import org.apache.log4j.Logger;

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

	private static void addCometSession(final CometSession cometSession) {
		LOGGER.debug("A new commet session was added.");
		final String sessionId = cometSession.getSessionID();

		cometSessionMap.put(sessionId, cometSession);
		if (serverPushConnectionListener != null) serverPushConnectionListener.onClientConnected(new GwtCometClientConnection(sessionId));
	}

	private static void removeCometSession(final CometSession cometSession) {
		LOGGER.debug("A commet session was removed.");

		cometSessionMap.remove(cometSession.getSessionID());
		if (serverPushConnectionListener != null) serverPushConnectionListener.onClientDisconnected(new GwtCometClientConnection(cometSession.getSessionID()));
	}

	@Override
	protected void doComet(final CometServletResponse cometResponse) throws ServletException, IOException {
		final CometSession cometSession = cometResponse.getSession(false);
		if (cometSession != null) {
			if (cometSession.isValid()) return;
			else removeCometSession(cometSession);
		}

		addCometSession(cometResponse.getSession(true));
	}

	public static class GwtCometServerHttpSessionListener implements HttpSessionListener {

		@Override
		public void sessionCreated(final HttpSessionEvent event) {}

		@Override
		public void sessionDestroyed(final HttpSessionEvent event) {
			for (final CometSession cometSession : cometSessionMap.values()) {
				if (cometSession.getHttpSession().getId().equals(event.getSession().getId())) {
					cometSession.invalidate();
					GwtCometServlet.removeCometSession(cometSession);
				}
			}
		}
	}
}
