package br.com.oncast.ontrack.shared.services.notification;

import java.util.Date;

import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class NotificationBuilder {

	private final Notification notification;

	public NotificationBuilder(final String message) {
		notification = new Notification();
		notification.setId(new UUID());
		notification.setTimestamp(new Date());
		notification.setMessage(message);
	}

	public NotificationBuilder addReceipient(final User receipient) {
		notification.addReceipient(receipient);
		return this;
	}

	public NotificationBuilder setTimestamp(final Date timestamp) {
		notification.setTimestamp(timestamp);
		return this;
	}

	public Notification getNotification() {
		return notification;
	}
}