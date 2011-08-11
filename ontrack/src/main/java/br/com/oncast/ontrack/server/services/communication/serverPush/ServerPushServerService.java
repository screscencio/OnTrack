package br.com.oncast.ontrack.server.services.communication.serverPush;

import br.com.oncast.ontrack.shared.services.communication.serverPush.ServerPushEvent;

public interface ServerPushServerService {

	void pushEvent(ServerPushEvent serverPushEvent);
}
