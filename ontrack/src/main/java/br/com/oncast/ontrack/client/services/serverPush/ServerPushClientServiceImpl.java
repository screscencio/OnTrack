package br.com.oncast.ontrack.client.services.serverPush;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.oncast.ontrack.client.services.identification.ClientIdentificationProvider;
import br.com.oncast.ontrack.client.services.notification.ClientNotificationService;
import br.com.oncast.ontrack.client.services.notification.NotificationConfirmationListener;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

import com.google.gwt.user.client.Window;

public class ServerPushClientServiceImpl implements ServerPushClientService {

	private final Map<Class<?>, List<ServerPushEventHandler<?>>> eventHandlersMap = new HashMap<Class<?>, List<ServerPushEventHandler<?>>>();
	private final ServerPushClient serverPushClient;

	public ServerPushClientServiceImpl(final ClientIdentificationProvider clientIdentificationProvider, final ClientNotificationService notificationService) {
		serverPushClient = new GwtCometClient(clientIdentificationProvider, new ServerPushClientEventListener() {

			@Override
			public void onConnected() {}

			@Override
			public void onDisconnected() {}

			@Override
			public void onEvent(final ServerPushEvent event) {
				processIncommingEvent(event);
			}

			/**
			 * @see br.com.oncast.ontrack.client.services.serverPush.ServerPushClientEventListener#onError(java.lang.Throwable)
			 *      This method is implemented using a Timer so that errors are not treated instantly, avoiding errors messages thrown because of
			 *      browser reload events (or when the user closes the window). When a user reloads the page the server push service crashes.
			 */
			@Override
			public void onError(final Throwable exception) {
				exception.printStackTrace();
				notificationService.showErrorWithConfirmation("No internet connection...", new NotificationConfirmationListener() {
					@Override
					public void onConfirmation() {
						Window.Location.reload();
					}
				});
			}
		});
		connect();
	}

	@Override
	public <T extends ServerPushEvent> void registerServerEventHandler(final Class<T> eventClass, final ServerPushEventHandler<T> serverPushEventHandler) {
		if (!eventHandlersMap.containsKey(eventClass)) eventHandlersMap.put(eventClass, new ArrayList<ServerPushEventHandler<?>>());

		final List<ServerPushEventHandler<?>> handlerList = eventHandlersMap.get(eventClass);
		handlerList.add(serverPushEventHandler);
	}

	private void processIncommingEvent(final ServerPushEvent event) {
		for (final ServerPushEventHandler<?> handler : eventHandlersMap.get(event.getClass())) {
			notifyEventHandler(handler, event);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void notifyEventHandler(final ServerPushEventHandler handler, final ServerPushEvent event) {
		handler.onEvent(event);
	}

	private void connect() {
		if (!serverPushClient.isRunning()) serverPushClient.start();
	}
}
