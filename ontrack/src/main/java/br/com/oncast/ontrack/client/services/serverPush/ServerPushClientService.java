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

	public ServerPushClientService() {
		serverPushClient = new GwtCometClient(new ServerPushClientEventListener() {
			@Override
			public void onConnected() {
				scheduleConnectionHealthMonitor();
			}

			@Override
			public void onDisconnected() {
				// FIXME
				Window.alert("The application was disconnected. It will try now to reconnect to server...");
				connect();
			}

			@Override
			public void onEvent(final ServerPushEvent event) {
				processIncommingEvent(event);
			}

			@Override
			public void onError(final Throwable exception) {
				// FIXME
				Window.alert("The application found an error while communicating with the server.\n\nException: " + exception.toString());

				// TODO +++Notify Error threatment service.
				threatSyncingError();
			}
		});
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

	// FIXME Remove this method. For now it is being used as a guarantee of service availability.
	private void scheduleConnectionHealthMonitor() {
		new Timer() {

			@Override
			public void run() {
				if (!serverPushClient.isRunning()) threatSyncingError();
				else scheduleConnectionHealthMonitor();
			}
		}.schedule(5000);
	}

	private void connect() {
		if (!serverPushClient.isRunning()) serverPushClient.start();
	}

	private void threatSyncingError() {
		// TODO +++Delegate treatment to Error threatment service eliminating the need for this method.
		Window.alert("The connection with the server was lost.\nCheck your internet connection...\n\nThe application will be briethly reloaded.");
		Window.Location.reload();
	}
}
