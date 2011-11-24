package br.com.oncast.ontrack.server.services.serverPush;

interface InternalConnectionListener {

	void onClientConnected(ServerPushConnection connection);

	void onClientDisconnected(ServerPushConnection connection);
}