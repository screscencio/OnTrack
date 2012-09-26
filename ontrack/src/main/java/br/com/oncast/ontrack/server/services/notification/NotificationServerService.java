package br.com.oncast.ontrack.server.services.notification;

import java.util.List;

import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateNotificationException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveNotificationListException;
import br.com.oncast.ontrack.shared.services.notification.Notification;

public interface NotificationServerService {

	List<Notification> retrieveCurrentUserNotificationList() throws UnableToRetrieveNotificationListException;

	void registerNewNotification(Notification notification) throws UnableToCreateNotificationException;

}
