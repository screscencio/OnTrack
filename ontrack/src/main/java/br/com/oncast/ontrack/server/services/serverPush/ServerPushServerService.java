package br.com.oncast.ontrack.server.services.serverPush;

import java.util.Collection;

import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

public interface ServerPushServerService {

	void pushEvent(ServerPushEvent serverPushEvent, Collection<ServerPushClient> clients);

	void registerConnectionListener(ServerPushConnectionListener listener);

	/**
	 * @return the client which is sending the event.
	 */
	ServerPushClient getSender();

}
