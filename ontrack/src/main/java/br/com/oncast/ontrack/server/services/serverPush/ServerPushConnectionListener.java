package br.com.oncast.ontrack.server.services.serverPush;

public interface ServerPushConnectionListener {

	void onClientConnected(ServerPushClient client);

	void onClientDisconnected(ServerPushClient client);
}
