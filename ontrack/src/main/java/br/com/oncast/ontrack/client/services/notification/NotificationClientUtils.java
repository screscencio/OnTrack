package br.com.oncast.ontrack.client.services.notification;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.NotificationRecipient;

public class NotificationClientUtils {

	public static NotificationRecipient getRecipientForCurrentUser(final Notification notification) {
		final UserRepresentation currentUser = ClientServiceProvider.getCurrentUser();
		if (currentUser == null) throw new RuntimeException("There is no user logged in.");
		return notification.getRecipient(currentUser.getId());
	}

	public static List<Notification> getUnreadNotificationsForCurrentUser(final List<Notification> notifications) {
		final List<Notification> unread = new ArrayList<Notification>();
		for (final Notification notification : notifications) {
			if (!ClientServiceProvider.getInstance().getNotificationService().isImportant(notification)) continue;

			final NotificationRecipient recipient = getRecipientForCurrentUser(notification);

			if (recipient == null) continue;
			if (!recipient.getReadState()) unread.add(notification);
		}
		return unread;
	}

}
