package br.com.oncast.ontrack.server.services.serverPush;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;

import net.zschech.gwt.comet.server.CometServlet;
import net.zschech.gwt.comet.server.CometServletResponse;
import net.zschech.gwt.comet.server.CometSession;

public class ServerPushServlet extends CometServlet {

	private static final long serialVersionUID = 1L;
	private static Set<CometSessionListener> cometSessionListenerSet = new HashSet<CometSessionListener>();

	@Override
	protected void doComet(final CometServletResponse cometResponse) throws ServletException, IOException {
		CometSession cometSession = cometResponse.getSession(false);
		if (cometSession == null) {
			cometSession = cometResponse.getSession();
			notifyListenersOnSessionCreated(cometSession);
		}
	}

	@Override
	public void cometTerminated(final CometServletResponse cometResponse, final boolean serverInitiated) {
		notifyListenersOnSessionDestroyed(cometResponse.getSession());
	}

	static void addCometSessionListener(final CometSessionListener listener) {
		cometSessionListenerSet.add(listener);
	}

	private void notifyListenersOnSessionCreated(final CometSession cometSession) {
		for (final CometSessionListener listener : cometSessionListenerSet) {
			listener.onSessionCreated(cometSession);
		}
	}

	private void notifyListenersOnSessionDestroyed(final CometSession cometSession) {
		for (final CometSessionListener listener : cometSessionListenerSet) {
			listener.onSessionDestroyed(cometSession);
		}
	}
}
