package br.com.oncast.ontrack.client.services.notification;

import java.util.Set;

import br.com.oncast.ontrack.shared.services.notification.Notification;

public interface NotificationListChangeListener {

	void onNotificationListChanged(Set<Notification> notifications);

	void onNotificationListAvailabilityChange(boolean availability);
}
