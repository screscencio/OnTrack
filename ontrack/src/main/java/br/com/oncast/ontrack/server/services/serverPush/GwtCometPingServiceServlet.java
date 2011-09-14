package br.com.oncast.ontrack.server.services.serverPush;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.client.services.serverPush.GwtCometPingService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GwtCometPingServiceServlet extends RemoteServiceServlet implements GwtCometPingService {

	private static final Logger LOGGER = Logger.getLogger(GwtCometPingServiceServlet.class);

	private static final long serialVersionUID = 1L;

	@Override
	public void ping() {
		final HttpSession session = getThreadLocalRequest().getSession();
		LOGGER.debug("Received ping from client (session: '" + session.getId() + "'). ");
	}
}
