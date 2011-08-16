package br.com.oncast.ontrack.server.services.serverPush;

import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

public interface ServerPushServerService {

	void pushEvent(ServerPushEvent serverPushEvent);
}
