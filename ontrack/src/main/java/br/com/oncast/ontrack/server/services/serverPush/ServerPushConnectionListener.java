package br.com.oncast.ontrack.server.services.serverPush;

public interface ServerPushConnectionListener {

	void onClientConnected(ServerPushConnection connection);

	void onClientDisconnected(ServerPushConnection connection);
}
