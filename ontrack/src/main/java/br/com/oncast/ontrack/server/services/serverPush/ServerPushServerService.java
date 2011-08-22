package br.com.oncast.ontrack.server.services.serverPush;

import java.util.Set;

import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

public interface ServerPushServerService {

	void pushEvent(ServerPushEvent serverPushEvent, Set<ServerPushConnection> clients);

	void registerConnectionListener(ServerPushConnectionListener listener);
}
