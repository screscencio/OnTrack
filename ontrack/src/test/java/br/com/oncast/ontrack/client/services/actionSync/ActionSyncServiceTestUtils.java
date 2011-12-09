package br.com.oncast.ontrack.client.services.actionSync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.errorHandling.ErrorTreatmentService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushEventHandler;
import br.com.oncast.ontrack.server.services.notification.NotificationService;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEvent;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

// FIXME Remove after refactoring ActionQueuedDispatcherTest
public class ActionSyncServiceTestUtils {

	protected class ValueHolder<T> {
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

	protected final class ServerPushClientServiceMockImpl implements ServerPushClientService {
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
	}

	private ServerPushClientServiceMockImpl serverPushClientService;
	private NotificationService notificationService;
	private ErrorTreatmentService errorTreatmentService;

	private ServerPushClientServiceMockImpl getServerPushClientServiceMock() {
		if (serverPushClientService != null) return serverPushClientService;
		return serverPushClientService = new ServerPushClientServiceMockImpl();
	}

	public NotificationService getNotificationServiceMock() {
		if (notificationService != null) return notificationService;

		final ServerPushClientServiceMockImpl serverPushClientServiceMock = getServerPushClientServiceMock();
		return notificationService = new NotificationService() {

			@Override
			public void notifyActions(final ModelActionSyncRequest modelActionSyncRequest) {
				serverPushClientServiceMock.processIncommingEvent(new ServerActionSyncEvent(modelActionSyncRequest));
			}

			@Override
			public void notifyProjectCreation(final long userId, final ProjectRepresentation projectRepresentation) {
				}
		};
	}

	public ErrorTreatmentService getErrorTreatmentServiceMock() {
		if (errorTreatmentService != null) return errorTreatmentService;
		return errorTreatmentService = new ErrorTreatmentService() {

			@Override
			public void treatFatalError(final String errorDescriptionMessage) {
				Assert.fail(errorDescriptionMessage);
			}

			@Override
			public void treatFatalError(final String errorDescriptionMessage, final Throwable caught) {
				caught.printStackTrace();
				Assert.fail(errorDescriptionMessage);
			}

			@Override
			public void treatUserWarning(final String string, final Exception e) {
				// Purposefully ignored exception
			}
		};
	}

	public ProjectRepresentationProvider getProjectRepresentationProviderMock() {
		final ProjectRepresentationProviderMock projectRepresentationProvider = new ProjectRepresentationProviderMock();
		projectRepresentationProvider.setProjectRepresentation(new ProjectRepresentation(1, "Default project"));
		return projectRepresentationProvider;
	}
}
