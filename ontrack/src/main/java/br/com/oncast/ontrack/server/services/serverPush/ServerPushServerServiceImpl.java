package br.com.oncast.ontrack.server.services.serverPush;

import java.util.ArrayList;
import java.util.List;

import net.zschech.gwt.comet.server.CometServlet;
import net.zschech.gwt.comet.server.CometSession;
import br.com.oncast.ontrack.server.services.httpSessionProvider.HttpSessionProvider;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

// FIXME Should this service be asynchronous? (Run in another thread so that it does not affect this client´s thread)
public class ServerPushServerServiceImpl implements ServerPushServerService {

	private final HttpSessionProvider httpSessionProvider;
	private final List<CometSession> cometSessionList;

	public ServerPushServerServiceImpl(final HttpSessionProvider httpSessionProvider) {
		this.httpSessionProvider = httpSessionProvider;
		cometSessionList = new ArrayList<CometSession>();
	}

	@Override
	public void pushEvent(final ServerPushEvent serverPushEvent) {
		final List<CometSession> removedSessions = new ArrayList<CometSession>();
		final CometSession callerCometSession = CometServlet.getCometSession(httpSessionProvider.getCurrentSession());
		for (final CometSession cometSession : cometSessionList) {
			try {
				if (cometSession.equals(callerCometSession)) continue;
				cometSession.enqueue(serverPushEvent);
			}
			catch (final IllegalStateException e) {
				System.out.println("The session is being removed from server push channel.");
				removedSessions.add(cometSession);
			}
		}

		cometSessionList.removeAll(removedSessions);
	}
}
