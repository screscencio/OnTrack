package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.util.List;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;
import br.com.oncast.ontrack.shared.services.notification.Notification;

public class NotificationListResponse implements DispatchResponse {

	private List<Notification> notificationList;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected NotificationListResponse() {}

	public NotificationListResponse(final List<Notification> list) {
		this.notificationList = list;
	}

	public List<Notification> getNotificationList() {
		return notificationList;
	}
}