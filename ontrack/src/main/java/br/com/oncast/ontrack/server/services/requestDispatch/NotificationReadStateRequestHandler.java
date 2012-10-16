package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.notification.NotificationServerService;
import br.com.oncast.ontrack.shared.services.requestDispatch.NotificationReadStateRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.NotificationReadStateResponse;

public class NotificationReadStateRequestHandler implements RequestHandler<NotificationReadStateRequest, NotificationReadStateResponse> {

	private static final NotificationServerService NOTIFICATION_SERVICE = ServerServiceProvider.getInstance().getNotificationServerService();

	@Override
	public NotificationReadStateResponse handle(final NotificationReadStateRequest request) throws Exception {
		NOTIFICATION_SERVICE.updateNotificationCurrentUserReadState(request.getNotification(), request.getState());
		return new NotificationReadStateResponse();
	}
}