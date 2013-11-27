package br.com.oncast.ontrack.server.services.serverPush;

import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

import java.util.Set;

public interface ServerPushServerService {

	/**
	 * Pushes an event to a set of clients, referenced by their clientIds.
	 * If a clientId does not reference any client connection (ant therefore is invalid) it is ignored.
	 * 
	 * @param serverPushEvent the event to be sent.
	 * @param clients the set of clientIds of the event recipients.
	 */
	void pushEvent(ServerPushEvent serverPushEvent, Set<ServerPushConnection> clients);

	void registerConnectionListener(ServerPushConnectionListener listener);
}
