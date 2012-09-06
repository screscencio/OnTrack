package br.com.oncast.ontrack.server.services.serverPush;

import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

public interface ServerPushApi {

	void pushEvent(ServerPushEvent serverPushEvent, ServerPushConnection client);

	void setConnectionListener(InternalConnectionListener connectionListener);
}