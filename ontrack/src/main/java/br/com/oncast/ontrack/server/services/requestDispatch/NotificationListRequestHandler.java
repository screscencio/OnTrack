package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.notification.NotificationServerService;
import br.com.oncast.ontrack.shared.services.requestDispatch.NotificationListRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.NotificationListResponse;

public class NotificationListRequestHandler implements RequestHandler<NotificationListRequest, NotificationListResponse> {

	private static final NotificationServerService NOTIFICATION_SERVICE = ServerServiceProvider.getInstance().getNotificationServerService();

	@Override
	public NotificationListResponse handle(final NotificationListRequest request) throws Exception {
		return new NotificationListResponse(NOTIFICATION_SERVICE.retrieveCurrentUserNotificationList());
	}

}