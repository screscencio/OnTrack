package br.com.oncast.ontrack.client.services.serverPush;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.atmosphere.gwt.client.AtmosphereListener;

import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.serverPush.atmosphere.OntrackAtmosphereClient;
import br.com.oncast.ontrack.client.ui.places.loading.ServerPushConnectionCallback;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

import com.google.gwt.event.shared.HandlerRegistration;

public class ServerPushClientServiceImpl implements ServerPushClientService {

	private final Map<Class<?>, List<ServerPushEventHandler<?>>> eventHandlersMap = new HashMap<Class<?>, List<ServerPushEventHandler<?>>>();
	private final ServerPushClient serverPushClient;
	private final List<ServerPushConnectionCallback> serverPushConnectionCallbacks = new ArrayList<ServerPushConnectionCallback>();
	private final AtmosphereListener atmosphereListener;

	public ServerPushClientServiceImpl(final ClientAlertingService alertingService, final ClientErrorMessages messages) {
		atmosphereListener = new AtmosphereListener() {

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
				serverPushClient.stop();
				notifyError(exception);
				exception.printStackTrace();
			}

			@Override
			public void onDisconnected() {
				notifyDisconnection();
			}

			@Override
			public void onConnected(final int heartbeat, final int connectionID) {
				notifyConnection();
			}

			@Override
			public void onBeforeDisconnected() {}

			@Override
			public void onAfterRefresh() {}
		};
		serverPushClient = new OntrackAtmosphereClient(atmosphereListener);
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

	@Override
	public void connect() {
		serverPushClient.start();
	}

	@Override
	public String getConnectionID() {
		if (serverPushClient == null) return null;
		return String.valueOf(serverPushClient.getConnectionId());
	}

	@Override
	public boolean isConnected() {
		if (serverPushClient == null) return false;
		return serverPushClient.isRunning() && serverPushClient.getConnectionId() > 0;
	}

	@Override
	public void addConnectionListener(final ServerPushConnectionCallback serverPushConnectionCallback) {
		serverPushConnectionCallbacks.add(serverPushConnectionCallback);
		if (isConnected()) serverPushConnectionCallback.connected();
	}

	@Override
	public void removeConnectionListener(final ServerPushConnectionCallback serverPushConnectionCallback) {
		serverPushConnectionCallbacks.remove(serverPushConnectionCallback);
	}

	protected void notifyConnection() {
		for (final ServerPushConnectionCallback serverPushConnectionCallback : new ArrayList<ServerPushConnectionCallback>(serverPushConnectionCallbacks))
			serverPushConnectionCallback.connected();
	}

	protected void notifyDisconnection() {
		for (final ServerPushConnectionCallback serverPushConnectionCallback : new ArrayList<ServerPushConnectionCallback>(serverPushConnectionCallbacks))
			serverPushConnectionCallback.disconnected();
	}

	protected void notifyError(final Throwable cause) {
		for (final ServerPushConnectionCallback serverPushConnectionCallback : new ArrayList<ServerPushConnectionCallback>(serverPushConnectionCallbacks))
			serverPushConnectionCallback.uncaughtExeption(cause);
	}
}
