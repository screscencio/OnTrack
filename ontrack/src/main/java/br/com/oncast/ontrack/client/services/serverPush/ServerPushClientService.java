package br.com.oncast.ontrack.client.services.serverPush;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerPushClientService {

	private final Map<Class<?>, List<ServerPushEventHandler<?>>> eventHandlersMap = new HashMap<Class<?>, List<ServerPushEventHandler<?>>>();
	private final GwtCometClient serverPushClient;

	public ServerPushClientService() {
		serverPushClient = new GwtCometClient(new ServerPushClientEventListener() {
			@Override
			public void onEvent(final Serializable event) {
				processIncommingEvent(event);
			}
		});
		serverPushClient.connectToServer();
	}

	public void registerServerEventHandler(final ServerPushEventHandler<?> serverPushEventHandler) {
		final Class<?> eventClass = serverPushEventHandler.getHandledEventClass();
		if (!eventHandlersMap.containsKey(eventClass)) eventHandlersMap.put(eventClass, new ArrayList<ServerPushEventHandler<?>>());

		final List<ServerPushEventHandler<?>> handlerList = eventHandlersMap.get(eventClass);
		// TODO Potentially verify the if handlerList already contains this handler.
		handlerList.add(serverPushEventHandler);
	}

	private void processIncommingEvent(final Serializable event) {
		final List<ServerPushEventHandler<?>> handlers = eventHandlersMap.get(event.getClass());
		notifyEventHandlers(handlers, event);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void notifyEventHandlers(final List<ServerPushEventHandler<?>> eventHandlers, final Serializable event) {
		for (final ServerPushEventHandler handler : eventHandlers) {
			handler.onEvent(event);
		}
	}

}
