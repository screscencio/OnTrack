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
import br.com.oncast.ontrack.shared.exceptions.business.UnableToUpdateNotificationException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.NotificationCreatedEvent;
import br.com.oncast.ontrack.shared.services.notification.NotificationRecipient;

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
			return persistenceService.retrieveLatestNotificationsForUser(new UserRepresentation(user.getId()), MAX_NUMBER_OF_NOTIFICATIONS);
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
		final List<UUID> recipientsAsUserMails = notification.getRecipientsAsUserIds();
		try {
			final List<User> usersByIds = persistenceService.retrieveUsersByIds(recipientsAsUserMails);
			this.multicastService.multicastToUsers(new NotificationCreatedEvent(notification), usersByIds);
		}
		catch (final PersistenceException e) {
			final String message = "Unable to multicast new notification: Unable to retrieve recipient list.";
			LOGGER.error(message, e);
			throw new UnableToCreateNotificationException(message);
		}
	}

	@Override
	public void updateNotificationCurrentUserReadState(final Notification notification, final boolean read) throws UnableToUpdateNotificationException {
		final User user = this.authenticationManager.getAuthenticatedUser();
		final NotificationRecipient recipient = notification.getRecipient(user);

		if (recipient == null) {
			final String message = "Unable to update notification: The current user is not in the notification recipients.";
			LOGGER.error(message);
			throw new UnableToUpdateNotificationException(message);
		}

		recipient.setReadState(read);

		try {
			persistenceService.persistOrUpdateNotification(notification);
		}
		catch (final PersistenceException e) {
			final String message = "Unable to update notification: Unable to persist it.";
			LOGGER.error(message, e);
			throw new UnableToUpdateNotificationException(message);
		}
	}

}
