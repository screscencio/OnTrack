package br.com.oncast.ontrack.client.services.serverPush;

import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

interface ServerPushClientEventListener {

	void onEvent(ServerPushEvent event);

	void onConnected();

	void onDisconnected();

	void onError(Throwable exception);
}
