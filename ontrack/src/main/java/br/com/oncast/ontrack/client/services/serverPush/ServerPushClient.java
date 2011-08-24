package br.com.oncast.ontrack.client.services.serverPush;

interface ServerPushClient {

	void start();

	boolean isRunning();

	void stop();
}