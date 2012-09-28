package br.com.oncast.ontrack.client.services.notification;

import java.util.List;

import br.com.oncast.ontrack.shared.services.notification.Notification;

public interface NotificationListChangeListener {

	void onNotificationListChanged(List<Notification> notifications);

	void onNotificationListAvailabilityChange(boolean availability);
}
