package br.com.oncast.ontrack.client.services.notification;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.NotificationRecipient;

public class NotificationClientUtils {

	public static NotificationRecipient getRecipientForCurrentUser(final Notification notification) {
		final UUID authenticatedUserId = ClientServices.getCurrentUser();
		if (authenticatedUserId == null) throw new RuntimeException("There is no user logged in.");
		return notification.getRecipient(authenticatedUserId);
	}

	public static List<Notification> getUnreadNotificationsForCurrentUser(final List<Notification> notifications) {
		final List<Notification> unread = new ArrayList<Notification>();
		for (final Notification notification : notifications) {
			if (!ClientServices.get().notifications().isImportant(notification)) continue;

			final NotificationRecipient recipient = getRecipientForCurrentUser(notification);

			if (recipient == null) continue;
			if (!recipient.getReadState()) unread.add(notification);
		}
		return unread;
	}

}
