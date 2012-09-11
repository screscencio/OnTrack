package br.com.oncast.ontrack.client.services.serverPush;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.atmosphere.gwt.client.AtmosphereListener;

import br.com.oncast.ontrack.client.services.notification.ClientNotificationService;
import br.com.oncast.ontrack.client.services.notification.NotificationConfirmationListener;
import br.com.oncast.ontrack.client.services.serverPush.atmosphere.OntrackAtmosphereClient;
import br.com.oncast.ontrack.client.ui.places.loading.ServerPushConnectionCallback;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

import com.google.gwt.user.client.Window;

public class ServerPushClientServiceImpl implements ServerPushClientService {

	private final Map<Class<?>, List<ServerPushEventHandler<?>>> eventHandlersMap = new HashMap<Class<?>, List<ServerPushEventHandler<?>>>();
	private final ServerPushClient serverPushClient;
	private ServerPushConnectionCallback serverPushConnectionCallback;

	public ServerPushClientServiceImpl(final ClientNotificationService notificationService) {
		serverPushClient = new OntrackAtmosphereClient(new AtmosphereListener() {

			@Override
			public void onRefresh() {}

			@Override
			public void onMessage(final List<?> messages) {
				processIncommingEvent(messages);
			}

			@Override
			public void onHeartbeat() {}

			@Override
			public void onError(final Throwable exception, final boolean connected) {
				// FIXME Mats think about how to update current model without being annoying to the user
				notificationService.showErrorWithConfirmation("No internet connection...", new NotificationConfirmationListener() {
					@Override
					public void onConfirmation() {
						Window.Location.reload();
					}
				});
			}

			@Override
			public void onDisconnected() {}

			@Override
			public void onConnected(final int heartbeat, final int connectionID) {
				serverPushConnectionCallback.connected();
				serverPushConnectionCallback = null;
			}

			@Override
			public void onBeforeDisconnected() {}

			@Override
			public void onAfterRefresh() {}
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

	private void processIncommingEvent(final List<?> messages) {
		for (final Object object : messages) {
			if (object instanceof ServerPushEvent) processIncommingEvent((ServerPushEvent) object);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void notifyEventHandler(final ServerPushEventHandler handler, final ServerPushEvent event) {
		handler.onEvent(event);
	}

	private void connect() {
		serverPushClient.start();
	}

	@Override
	public String getConnectionID() {
		return String.valueOf(serverPushClient.getConnectionId());
	}

	@Override
	public boolean isConnected() {
		return serverPushClient.isRunning() && serverPushClient.getConnectionId() > 0;
	}

	@Override
	public void onConnected(final ServerPushConnectionCallback serverPushConnectionCallback) {
		if (isConnected()) serverPushConnectionCallback.connected();
		else this.serverPushConnectionCallback = serverPushConnectionCallback;
	}
}
