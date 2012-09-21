package br.com.oncast.ontrack.server.services.notification;

import java.util.List;

import br.com.oncast.ontrack.shared.services.notification.Notification;

public interface NotificationServerService {

	List<Notification> retrieveCurrentUserNotificationList();

}
