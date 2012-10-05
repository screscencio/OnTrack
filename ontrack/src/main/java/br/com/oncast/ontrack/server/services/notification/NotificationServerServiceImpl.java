package br.com.oncast.ontrack.server.services.notification;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateNotificationException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveNotificationListException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.NotificationCreatedEvent;

public class NotificationServerServiceImpl implements NotificationServerService {

	private static final Logger LOGGER = Logger.getLogger(NotificationServerServiceImpl.class);
	protected static final int MAX_NUMBER_OF_NOTIFICATIONS = 50;

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
	public List<Notification> retrieveCurrentUserNotificationList() throws UnableToRetrieveNotificationListException {
		final User user = this.authenticationManager.getAuthenticatedUser();
		LOGGER.debug("Retrieving notifications for user '" + user + "'.");
		try {
			return persistenceService.retrieveLatestNotificationsForUser(user, MAX_NUMBER_OF_NOTIFICATIONS);
		}
		catch (final NoResultFoundException e) {
			return new ArrayList<Notification>();
		}
		catch (final PersistenceException e) {
			final String message = "It was not possible to retrieve the user's notifications.";
			LOGGER.error(message, e);
			throw new UnableToRetrieveNotificationListException(message);
		}
	}

	@Override
	public void registerNewNotification(final Notification notification) throws UnableToCreateNotificationException {
		try {
			this.persistenceService.persistOrUpdateNotification(notification);
		}
		catch (final PersistenceException e) {
			final String message = "Unable to create a new notification exception.";
			LOGGER.error(message, e);
			throw new UnableToCreateNotificationException(message);
		}
		final List<String> recipientsAsUserMails = notification.getRecipientsAsUserMails();
		try {
			final List<User> usersByEmails = persistenceService.retrieveUsersByEmails(recipientsAsUserMails);
			this.multicastService.multicastToUsers(new NotificationCreatedEvent(notification), usersByEmails);
		}
		catch (final PersistenceException e) {
			final String message = "Unable to multicast new notification: Unable to retrieve recipient list.";
			LOGGER.error(message, e);
			throw new UnableToCreateNotificationException(message);
		}
	}
}
