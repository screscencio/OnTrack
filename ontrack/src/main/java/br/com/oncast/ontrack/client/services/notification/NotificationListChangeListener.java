package br.com.oncast.ontrack.client.services.notification;

import br.com.oncast.ontrack.shared.services.notification.Notification;

import java.util.List;

public interface NotificationListChangeListener {

	void onNotificationListChanged(List<Notification> notifications);

	void onNotificationListAvailabilityChange(boolean availability);
}
