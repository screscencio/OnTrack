package br.com.oncast.ontrack.server.services.notification;

import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateNotificationException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveNotificationListException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToUpdateNotificationException;
import br.com.oncast.ontrack.shared.services.notification.Notification;

import java.util.List;

public interface NotificationServerService {

	List<Notification> retrieveCurrentUserNotificationList() throws UnableToRetrieveNotificationListException;

	void registerNewNotification(Notification notification) throws UnableToCreateNotificationException;

	void updateNotificationCurrentUserReadState(Notification notification, boolean read) throws UnableToUpdateNotificationException;

}
