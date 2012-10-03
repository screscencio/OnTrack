package br.com.oncast.ontrack.shared.services.notification;

import java.util.Date;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.notification.Notification.NotificationType;

public class NotificationBuilder {

	private final Notification notification;

	public NotificationBuilder(final NotificationType type, final ProjectRepresentation projectRepresentation, final User author) {
		notification = new Notification();
		notification.setId(new UUID());
		notification.setTimestamp(new Date());
		notification.setType(type);
		notification.setProjectRepresentation(projectRepresentation);
		notification.setAuthor(author);
	}

	public NotificationBuilder addReceipient(final User receipient) {
		notification.addReceipient(new NotificationRecipient(receipient));
		return this;
	}

	public NotificationBuilder setTimestamp(final Date timestamp) {
		notification.setTimestamp(timestamp);
		return this;
	}

	public NotificationBuilder setReferenceId(final UUID id) {
		notification.setReferenceId(id);
		return this;
	}

	public NotificationBuilder setDescription(final String description) {
		notification.setDescription(description);
		return this;
	}

	public Notification getNotification() {
		return notification;
	}
}