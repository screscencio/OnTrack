package br.com.oncast.ontrack.server.services.notification;

import java.util.List;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.NotificationCreatedEvent;

public class NotificationServerServiceImpl implements NotificationServerService {

	private static final Logger LOGGER = Logger.getLogger(NotificationServerServiceImpl.class);
	private static final int MAX_NUMBER_OF_NOTIFICATIONS = 50;

	private final AuthenticationManager authenticationManager;
	private final PersistenceService persistenceService;
	private final MulticastService multicastService;

	public NotificationServerServiceImpl(final AuthenticationManager authenticationManager, final PersistenceService persistenceService,
			final MulticastService multicastService) {
		this.authenticationManager = authenticationManager;
		this.persistenceService = persistenceService;
		this.multicastService = multicastService;
	}

	@Override
	public List<Notification> retrieveCurrentUserNotificationList() {
		final User user = this.authenticationManager.getAuthenticatedUser();
		LOGGER.debug("Retrieving notifications for user '" + user + "'.");
		return persistenceService.retrieveLatestNotificationsForUser(user, MAX_NUMBER_OF_NOTIFICATIONS);
	}

	public void registerNewNotification(final Notification notification) {
		this.persistenceService.persistOrUpdateNotification(notification);
		this.multicastService.multicastToUsers(new NotificationCreatedEvent(notification), notification.getRecipients());
	}
}
