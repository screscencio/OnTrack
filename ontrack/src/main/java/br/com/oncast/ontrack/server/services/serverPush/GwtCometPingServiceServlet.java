package br.com.oncast.ontrack.server.services.serverPush;

import br.com.oncast.ontrack.client.services.serverPush.GwtCometPingService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GwtCometPingServiceServlet extends RemoteServiceServlet implements GwtCometPingService {

	private static final long serialVersionUID = 1L;

	@Override
	public void ping() {}
}
