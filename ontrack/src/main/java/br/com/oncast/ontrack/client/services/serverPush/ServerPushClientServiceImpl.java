package br.com.oncast.ontrack.client.services.serverPush;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.atmosphere.gwt.client.AtmosphereListener;

import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.alerting.AlertConfirmationListener;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.serverPush.atmosphere.OntrackAtmosphereClient;
import br.com.oncast.ontrack.client.ui.places.loading.ServerPushConnectionCallback;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class ServerPushClientServiceImpl implements ServerPushClientService {

	private final Map<Class<?>, List<ServerPushEventHandler<?>>> eventHandlersMap = new HashMap<Class<?>, List<ServerPushEventHandler<?>>>();
	private final ServerPushClient serverPushClient;
	private ServerPushConnectionCallback serverPushConnectionCallback;

	public ServerPushClientServiceImpl(final ClientAlertingService alertingService, final ClientErrorMessages messages) {
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
				exception.printStackTrace();
				alertingService.showErrorWithConfirmation(messages.noInternectConnection(), new AlertConfirmationListener() {
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
	public <T extends ServerPushEvent> HandlerRegistration registerServerEventHandler(final Class<T> eventClass,
			final ServerPushEventHandler<T> serverPushEventHandler) {
		if (!eventHandlersMap.containsKey(eventClass)) eventHandlersMap.put(eventClass, new ArrayList<ServerPushEventHandler<?>>());

		final List<ServerPushEventHandler<?>> handlerList = eventHandlersMap.get(eventClass);
		handlerList.add(serverPushEventHandler);

		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				handlerList.remove(serverPushEventHandler);
			}
		};
	}

	private void processIncommingEvent(final ServerPushEvent event) {
		if (!eventHandlersMap.containsKey(event.getClass())) return;

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
