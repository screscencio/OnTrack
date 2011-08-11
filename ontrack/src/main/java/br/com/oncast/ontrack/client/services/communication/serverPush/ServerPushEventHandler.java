package br.com.oncast.ontrack.client.services.communication.serverPush;

public interface ServerPushEventHandler<T> {

	void onEvent(T event);

	Class<T> getHandledEventClass();
}
