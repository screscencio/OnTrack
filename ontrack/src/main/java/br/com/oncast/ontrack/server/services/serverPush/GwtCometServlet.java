package br.com.oncast.ontrack.server.services.serverPush;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private static InternalConnectionListener serverPushConnectionListener;

	public GwtCometServlet() {}

	@Override
	public void setConnectionListener(final InternalConnectionListener serverPushConnectionListener) {
		GwtCometServlet.serverPushConnectionListener = serverPushConnectionListener;
	}

	@Override
	public void pushEvent(final ServerPushEvent serverPushEvent, final GwtCometClientConnection client) {
		try {
			cometSessionMap.get(client.getClientId()).enqueue(serverPushEvent);
		}
		catch (final IllegalStateException e) {
			// Purposefully ignored exception.
			// TODO Remove this print stack trace.
			e.printStackTrace();
			// TODO Analyze if removing a comet session would be necessary here. This exception is thrown exclusively when the client is disconnected.
			LOGGER.error("It was not possible to push event to client '" + client + "'.", e);
		}
	}

	private static void addCometSession(final CometSession cometSession) {
		LOGGER.debug("A new commet session was added (clientId='" + cometSession.getSessionID() + "', sessionId='" + cometSession.getHttpSession().getId()
				+ "').");
		LOGGER.debug("Commet sessions: " + cometSessionMap.size());

		cometSessionMap.put(cometSession.getSessionID(), cometSession);
		if (serverPushConnectionListener != null) serverPushConnectionListener.onClientConnected(createGwtCometClientConnection(cometSession));
	}

	private static void removeCometSession(final CometSession cometSession) {
		LOGGER.debug("A commet session was removed (clientId='" + cometSession.getSessionID() + "', sessionId='" + cometSession.getHttpSession().getId()
				+ "').");

		cometSessionMap.remove(cometSession.getSessionID());
		if (serverPushConnectionListener != null) serverPushConnectionListener.onClientDisconnected(createGwtCometClientConnection(cometSession));
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
		// TODO Analyze storing in the http session a specialized object that could manage comet sessions instead of just storing comet sessions using random
		// client ids.
		public void sessionDestroyed(final HttpSessionEvent event) {
			final List<CometSession> sessionsToBeRemoved = new ArrayList<CometSession>();

			for (final CometSession cometSession : cometSessionMap.values()) {
				if (cometSession.getHttpSession().getId().equals(event.getSession().getId())) {
					cometSession.invalidate();
					sessionsToBeRemoved.add(cometSession);
				}
			}

			for (final CometSession cometSession : sessionsToBeRemoved) {
				GwtCometServlet.removeCometSession(cometSession);
			}
		}
	}

	private static GwtCometClientConnection createGwtCometClientConnection(final CometSession cometSession) {
		return new GwtCometClientConnection(cometSession.getSessionID(), cometSession.getHttpSession().getId());
	}
}
