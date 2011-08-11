package br.com.oncast.ontrack.client.services.serverPush;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerPushClientService {

	private final Map<Class<?>, List<ServerPushEventHandler<?>>> eventHandlersMap = new HashMap<Class<?>, List<ServerPushEventHandler<?>>>();

	public void registerServerEventHandler(final ServerPushEventHandler<?> serverPushEventHandler) {
		final Class<?> eventClass = serverPushEventHandler.getHandledEventClass();
		if (!eventHandlersMap.containsKey(eventClass)) eventHandlersMap.put(eventClass, new ArrayList<ServerPushEventHandler<?>>());

		final List<ServerPushEventHandler<?>> handlerList = eventHandlersMap.get(eventClass);
		// TODO Potentially verify the if handlerList already contains this handler.
		handlerList.add(serverPushEventHandler);
	}
}
