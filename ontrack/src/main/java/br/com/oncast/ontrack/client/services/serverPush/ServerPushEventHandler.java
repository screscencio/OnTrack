package br.com.oncast.ontrack.client.services.serverPush;

public interface ServerPushEventHandler<T> {

	void onEvent(T event);
}
