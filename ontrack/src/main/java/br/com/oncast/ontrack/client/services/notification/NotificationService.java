package br.com.oncast.ontrack.client.services.notification;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;

import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.context.ProjectListChangeListener;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.NotificationCreatedEvent;
import br.com.oncast.ontrack.shared.services.notification.NotificationCreatedEventHandler;
import br.com.oncast.ontrack.shared.services.notification.NotificationType;
import br.com.oncast.ontrack.shared.services.requestDispatch.NotificationListRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.NotificationListResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.NotificationReadStateRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.NotificationReadStateResponse;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class NotificationService {

	private final DispatchService dispatchService;
	private final ClientAlertingService alertingService;
	private final Set<NotificationListChangeListener> notificationListChangeListeners = new HashSet<NotificationListChangeListener>();
	private final Set<NotificationReadStateChangeListener> notificationReadStateChangeListeners = new HashSet<NotificationReadStateChangeListener>();
	private final List<Notification> availableNotifications = new LinkedList<Notification>();
	private boolean notificationListAvailability;

	private static final Set<NotificationType> IMPORTANT_NOTIFICATIONS = new HashSet<NotificationType>();

	static {
		IMPORTANT_NOTIFICATIONS.add(NotificationType.IMPEDIMENT_SOLVED);
		IMPORTANT_NOTIFICATIONS.add(NotificationType.IMPEDIMENT_CREATED);
		IMPORTANT_NOTIFICATIONS.add(NotificationType.TEAM_INVITED);
		IMPORTANT_NOTIFICATIONS.add(NotificationType.TEAM_REMOVED);
	}

	public NotificationService(final DispatchService dispatchService, final ServerPushClientService serverPushClientService,
			final ProjectRepresentationProvider projectRepresentationProvider, final ClientAlertingService alertingService) {

		this.dispatchService = dispatchService;
		this.alertingService = alertingService;

		projectRepresentationProvider.registerProjectListChangeListener(new ProjectListChangeListener() {

			@Override
			public void onProjectListChanged(final Set<ProjectRepresentation> projectRepresentations) {}

			@Override
			public void onProjectNameUpdate(final ProjectRepresentation projectRepresentation) {}

			@Override
			public void onProjectListAvailabilityChange(final boolean availability) {
				if (availability) {
					updateAvailableNotifications();
					return;
				}

				availableNotifications.clear();
				notificationListAvailability = false;

				notifyNotificationListContentChange();
				notifyNotificationListAvailabilityChange();
			}
		});

		serverPushClientService.registerServerEventHandler(NotificationCreatedEvent.class, new NotificationCreatedEventHandler() {

			@Override
			public void onEvent(final NotificationCreatedEvent event) {
				final Notification newNotification = event.getNotification();
				if (availableNotifications.contains(newNotification)) return;
				availableNotifications.add(0, newNotification);

				notifyNotificationListContentChange();
			}
		});
	}

	public void updateNotificationReadState(final Notification notification, final boolean readState, final NotificationReadStateUpdateCallback callback) {
		dispatchService.dispatch(new NotificationReadStateRequest(notification, readState), new DispatchCallback<NotificationReadStateResponse>() {

			@Override
			public void onSuccess(final NotificationReadStateResponse result) {
				callback.notificationReadStateUpdated();
				notifyNotificationReadStateChangeListeners(notification, readState);
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {
				callback.onError();
			}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				callback.onError();
			}
		});
	}

	private void updateAvailableNotifications() {
		dispatchService.dispatch(new NotificationListRequest(), new DispatchCallback<NotificationListResponse>() {

			@Override
			public void onSuccess(final NotificationListResponse response) {
				availableNotifications.clear();
				availableNotifications.addAll(response.getNotificationList());
				notificationListAvailability = true;

				notifyNotificationListContentChange();
				notifyNotificationListAvailabilityChange();
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				// TODO +++Treat fatal error. Could not load project list...
				alertingService.showWarning("Notification list unavailable. Verify your connection.");
			}
		});
	}

	private void notifyNotificationListAvailabilityChange() {
		for (final NotificationListChangeListener listener : notificationListChangeListeners)
			notifyListenerForNotificationListAvailabilityChange(listener);
	}

	private void notifyNotificationListContentChange() {
		for (final NotificationListChangeListener listener : notificationListChangeListeners)
			notifyListenerForNotificationListChange(listener);
	}

	private void notifyListenerForNotificationListChange(final NotificationListChangeListener listener) {
		listener.onNotificationListChanged(availableNotifications);
	}

	protected void notifyListenerForNotificationListAvailabilityChange(final NotificationListChangeListener listener) {
		listener.onNotificationListAvailabilityChange(notificationListAvailability);
	}

	public void registerNotificationListChangeListener(final NotificationListChangeListener notificationListChangeListener) {
		if (!notificationListChangeListeners.contains(notificationListChangeListener)) notificationListChangeListeners.add(notificationListChangeListener);

		notifyListenerForNotificationListChange(notificationListChangeListener);
		notifyListenerForNotificationListAvailabilityChange(notificationListChangeListener);
	}

	public void unregisterNotificationListChangeListener(final NotificationListChangeListener notificationListChangeListener) {
		notificationListChangeListeners.remove(notificationListChangeListener);
	}

	public void registerNotificationReadStateChangeListener(final NotificationReadStateChangeListener notificationReadStateChangeListener) {
		notificationReadStateChangeListeners.add(notificationReadStateChangeListener);
	}

	protected void notifyNotificationReadStateChangeListeners(final Notification notification, final boolean readState) {
		for (final NotificationReadStateChangeListener listener : notificationReadStateChangeListeners) {
			listener.readStateChanged(notification, readState);
		}
	}

	public boolean isImportant(final Notification notification) {
		return IMPORTANT_NOTIFICATIONS.contains(notification.getType());
	}
}
