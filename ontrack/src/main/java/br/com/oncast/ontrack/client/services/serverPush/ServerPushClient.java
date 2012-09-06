package br.com.oncast.ontrack.client.services.serverPush;

public interface ServerPushClient {

	void start();

	boolean isRunning();

	void stop();

	int getConnectionId();
}