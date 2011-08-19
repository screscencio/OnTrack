package br.com.oncast.ontrack.server.services.serverPush;

import br.com.oncast.ontrack.server.services.httpSessionProvider.HttpSessionProvider;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

interface ServerPushApi {

	void pushEvent(ServerPushEvent serverPushEvent, GwtCometClientConnection client);

	ServerPushConnection getCurrentClient(HttpSessionProvider httpSessionProvider) throws ServerPushException;

	void setServerPushConnectionListener(ServerPushConnectionListener serverPushConnectionListener);
}