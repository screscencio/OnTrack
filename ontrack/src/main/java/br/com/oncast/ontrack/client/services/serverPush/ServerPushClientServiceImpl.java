package br.com.oncast.ontrack.client.services.serverPush;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.oncast.ontrack.client.services.errorHandling.ErrorTreatmentService;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

import com.google.gwt.user.client.Timer;

public class ServerPushClientServiceImpl implements ServerPushClientService {

	private final Map<Class<?>, List<ServerPushEventHandler<?>>> eventHandlersMap = new HashMap<Class<?>, List<ServerPushEventHandler<?>>>();
	private final ServerPushClient serverPushClient;
	private final Timer connectionHealthMonitorTimer;

	public ServerPushClientServiceImpl(final ErrorTreatmentService errorTreatmentService) {
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
				errorTreatmentService.treatFatalError(
						"The connection with the server was lost.\nCheck your internet connection...\n\nThe application will be briethly reloaded.", exception);
			}
		});
		connectionHealthMonitorTimer = new Timer() {

			@Override
			public void run() {
				if (!serverPushClient.isRunning()) errorTreatmentService
						.treatFatalError("The server connection is down.\n\nThe application will be briethly reloaded.");
				else scheduleConnectionHealthMonitor();
			}
		};
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

	// TODO Analyze the necessity of this method and timer. For now it is being used as a guarantee of service availability.
	private void scheduleConnectionHealthMonitor() {
		connectionHealthMonitorTimer.cancel();
		connectionHealthMonitorTimer.schedule(5000);
	}

	private void connect() {
		if (!serverPushClient.isRunning()) serverPushClient.start();
	}
}
