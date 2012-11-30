package br.com.oncast.ontrack.client.services.actionSync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.client.FailureHandler;
import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushEventHandler;
import br.com.oncast.ontrack.client.ui.places.loading.ServerPushConnectionCallback;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;
import br.com.oncast.ontrack.shared.services.user.UserDataUpdateEvent;

public class ActionQueuedDispatcherTestUtils {

	private ServerPushClientServiceMockImpl serverPushClientService;
	private MulticastService multicastService;

	private ServerPushClientServiceMockImpl getServerPushClientServiceMock() {
		if (serverPushClientService != null) return serverPushClientService;
		return serverPushClientService = new ServerPushClientServiceMockImpl();
	}

	MulticastService getMulticastServiceMock() {
		if (multicastService != null) return multicastService;

		final ServerPushClientServiceMockImpl serverPushClientServiceMock = getServerPushClientServiceMock();
		return multicastService = new MulticastService() {

			@Override
			public void multicastToUser(final ServerPushEvent event, final User authenticatedUser) {}

			@Override
			public void multicastToUsers(final ServerPushEvent event, final List<User> recipients) {}

			@Override
			public void multicastToCurrentUserClientInSpecificProject(final ServerPushEvent event, final UUID projectId) {
				serverPushClientServiceMock.processIncommingEvent(event);
			}

			@Override
			public void multicastToAllUsersButCurrentUserClientInSpecificProject(final ServerPushEvent event, final UUID projectId) {
				serverPushClientServiceMock.processIncommingEvent(event);
			}

			@Override
			public void multicastToAllProjectsInUserAuthorizationList(final UserDataUpdateEvent event, final List<ProjectRepresentation> projectsList) {}

			@Override
			public void multicastToAllUsersInSpecificProject(final ServerPushEvent event, final UUID projectId) {}
		};
	}

	class ValueHolder<T> {
		T value;

		public ValueHolder(final T initialValue) {
			value = initialValue;
		}

		public T getValue() {
			return value;
		}

		public void setValue(final T value) {
			this.value = value;
		}
	}

	interface DispatchListener {
		void onDispatch(final ModelActionSyncRequest modelActionSyncRequest, DispatchCallback<VoidResult> callback);
	}

	final class DispatchRequestServiceTestImplementation implements DispatchService {
		private DispatchListener listener;

		public void registerDispatchListener(final DispatchListener listener) {
			this.listener = listener;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T extends DispatchRequest<R>, R extends DispatchResponse> void dispatch(final T request, final DispatchCallback<R> dispatchCallback) {
			if (!(request instanceof ModelActionSyncRequest)) throw new RuntimeException("The test should not try to dispatch '" + request.getClass().getName()
					+ "'.");
			if (listener == null) throw new RuntimeException("The listener was not set.");
			listener.onDispatch((ModelActionSyncRequest) request, (DispatchCallback<VoidResult>) dispatchCallback);
		}

		@Override
		public <T extends FailureHandler<R>, R extends Throwable> void addFailureHandler(final Class<R> throwableClass, final T handler) {
			throw new RuntimeException("The test should not use this method.");
		}
	}

	final class ServerPushClientServiceMockImpl implements ServerPushClientService {
		private final Map<Class<?>, List<ServerPushEventHandler<?>>> eventHandlersMap = new HashMap<Class<?>, List<ServerPushEventHandler<?>>>();

		@Override
		public <T extends ServerPushEvent> void registerServerEventHandler(final Class<T> eventClass, final ServerPushEventHandler<T> serverPushEventHandler) {
			if (!eventHandlersMap.containsKey(eventClass)) eventHandlersMap.put(eventClass, new ArrayList<ServerPushEventHandler<?>>());

			final List<ServerPushEventHandler<?>> handlerList = eventHandlersMap.get(eventClass);
			handlerList.add(serverPushEventHandler);
		}

		public void processIncommingEvent(final ServerPushEvent event) {
			for (final ServerPushEventHandler<?> handler : eventHandlersMap.get(event.getClass())) {
				notifyEventHandler(handler, event);
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private void notifyEventHandler(final ServerPushEventHandler handler, final ServerPushEvent event) {
			handler.onEvent(event);
		}

		@Override
		public String getConnectionID() {
			return "1";
		}

		@Override
		public boolean isConnected() {
			return true;
		}

		@Override
		public void onConnected(final ServerPushConnectionCallback serverPushConnectionCallback) {}
	}
}
