package br.com.oncast.ontrack.client.services.serverPush;

import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

public interface ServerPushClientService {

	public <T extends ServerPushEvent> void registerServerEventHandler(final Class<T> eventClass, final ServerPushEventHandler<T> serverPushEventHandler);

}