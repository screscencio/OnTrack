package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;

import br.com.oncast.ontrack.shared.services.notification.Notification;

public class NotificationReadStateRequest implements DispatchRequest<NotificationReadStateResponse> {

	private Notification notification;
	private boolean readState;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	public NotificationReadStateRequest() {}

	public NotificationReadStateRequest(final Notification notification, final boolean readState) {
		this.notification = notification;
		this.readState = readState;
	}

	public Notification getNotification() {
		return notification;
	}

	public boolean getState() {
		return readState;
	}
}
