package br.com.oncast.ontrack.server.services.serverPush;

import br.com.oncast.ontrack.server.services.httpSessionProvider.HttpSessionProvider;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

// FIXME Should this service be asynchronous? (Run in another thread so that it does not affect this client´s thread)
public class ServerPushServerServiceImpl implements ServerPushServerService {

	private final HttpSessionProvider httpSessionProvider;

	public ServerPushServerServiceImpl(final HttpSessionProvider httpSessionProvider) {
		this.httpSessionProvider = httpSessionProvider;
		// FIXME Auto-generated catch block
	}

	@Override
	public void pushEvent(final ServerPushEvent serverPushEvent) {
		// FIXME Should push events to clients.
		// FIXME Should push events specific clients.
	}

}
