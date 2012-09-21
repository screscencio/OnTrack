package br.com.oncast.ontrack.shared.services.notification;

import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

public class NotificationCreatedEvent implements ServerPushEvent {

	private static final long serialVersionUID = 1L;
	private Notification notification;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected NotificationCreatedEvent() {}

	public NotificationCreatedEvent(final Notification notification) {
		this.notification = notification;
	}

	public Notification getNotification() {
		return notification;
	}
}
