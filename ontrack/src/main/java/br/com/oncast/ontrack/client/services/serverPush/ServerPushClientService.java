package br.com.oncast.ontrack.client.services.serverPush;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class ServerPushClientService {

	private final Map<Class<?>, List<ServerPushEventHandler<?>>> eventHandlersMap = new HashMap<Class<?>, List<ServerPushEventHandler<?>>>();
	private final ServerPushClient serverPushClient;
	private final Timer connectionHealthMonitorTimer;

	public ServerPushClientService() {
		serverPushClient = new GwtCometClient(new ServerPushClientEventListener() {
			@Override
			public void onConnected() {
				scheduleConnectionHealthMonitor();
			}

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
				// TODO +++Notify Error threatment service.
				threatSyncingError("The connection with the server was lost.\nCheck your internet connection...\n\nThe application will be briethly reloaded.");
			}
		});
		connectionHealthMonitorTimer = new Timer() {

			@Override
			public void run() {
				if (!serverPushClient.isRunning()) threatSyncingError("The server connection is down.\n\nThe application will be briethly reloaded.");
				else scheduleConnectionHealthMonitor();
			}
		};
		connect();
	}

	public <T extends ServerPushEvent> void registerServerEventHandler(final Class<T> eventClass, final ServerPushEventHandler<T> serverPushEventHandler) {
		if (!eventHandlersMap.containsKey(eventClass)) eventHandlersMap.put(eventClass, new ArrayList<ServerPushEventHandler<?>>());

		final List<ServerPushEventHandler<?>> handlerList = eventHandlersMap.get(eventClass);
		handlerList.add(serverPushEventHandler);
	}

	private void processIncommingEvent(final ServerPushEvent event) {
		for (final ServerPushEventHandler<?> handler : eventHandlersMap.get(event.getClass())) {
			notifyEventHandlers(handler, event);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void notifyEventHandlers(final ServerPushEventHandler handler, final ServerPushEvent event) {
		handler.onEvent(event);
	}

	// TODO Analyze the necessity of this method and timer. For now it is being used as a guarantee of service availability.
	private void scheduleConnectionHealthMonitor() {
		connectionHealthMonitorTimer.cancel();
		connectionHealthMonitorTimer.schedule(5000);
	}

	private void connect() {
		if (!serverPushClient.isRunning()) serverPushClient.start();
	}

	private void threatSyncingError(final String message) {
		// TODO +++Delegate treatment to Error treatment service eliminating the need for this method.
		serverPushClient.stop();
		Window.alert(message);
		Window.Location.reload();
	}
}
