package br.com.oncast.ontrack.server.services.notification;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.email.MailService;
import br.com.oncast.ontrack.server.services.email.NotificationMail;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateNotificationException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveNotificationListException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToUpdateNotificationException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.NotificationCreatedEvent;
import br.com.oncast.ontrack.shared.services.notification.NotificationRecipient;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

public class NotificationServerServiceImpl implements NotificationServerService {

	private static final Logger LOGGER = Logger.getLogger(NotificationServerServiceImpl.class);
	protected static final int MAX_NUMBER_OF_NOTIFICATIONS = 50;

	private final AuthenticationManager authenticationManager;
	private final PersistenceService persistenceService;
	private final MulticastService multicastService;
	private final MailService mailService;

	public NotificationServerServiceImpl(final AuthenticationManager authenticationManager, final PersistenceService persistenceService, final MulticastService multicastService,
			final MailService mailService) {
		this.authenticationManager = authenticationManager;
		this.persistenceService = persistenceService;
		this.multicastService = multicastService;
		this.mailService = mailService;
	}

	@Override
	public List<Notification> retrieveCurrentUserNotificationList() throws UnableToRetrieveNotificationListException {
		final User user = this.authenticationManager.getAuthenticatedUser();
		LOGGER.debug("Retrieving notifications for user '" + user + "'.");
		try {
			return persistenceService.retrieveLatestNotificationsForUser(user.getId(), MAX_NUMBER_OF_NOTIFICATIONS);
		} catch (final NoResultFoundException e) {
			return new ArrayList<Notification>();
		} catch (final PersistenceException e) {
			final String message = "It was not possible to retrieve the user's notifications.";
			LOGGER.error(message, e);
			throw new UnableToRetrieveNotificationListException(message);
		}
	}

	@Override
	public void registerNewNotification(final Notification notification) throws UnableToCreateNotificationException, MessagingException, PersistenceException, NoResultFoundException {
		this.persistenceService.persistOrUpdateNotification(notification);
		final List<UUID> recipientsAsUserMails = notification.getRecipientsAsUserIds();

		final List<User> users = persistenceService.retrieveUsersByIds(recipientsAsUserMails);
		this.multicastService.multicastToUsers(new NotificationCreatedEvent(notification), users);
		final User author = persistenceService.retrieveUserById(notification.getAuthorId());
		final ProjectRepresentation project = persistenceService.retrieveProjectRepresentation(notification.getProjectReference());
		for (final User user : users) {
			if (notification.isImportant(user.getId())) {
				mailService.send(NotificationMail.getMail(notification, author, user, users, project));
			}
		}
	}

	@Override
	public void updateNotificationCurrentUserReadState(final Notification notification, final boolean read) throws UnableToUpdateNotificationException {
		final User user = this.authenticationManager.getAuthenticatedUser();
		final NotificationRecipient recipient = notification.getRecipient(user.getId());

		if (recipient == null) {
			final String message = "Unable to update notification: The current user is not in the notification recipients.";
			LOGGER.error(message);
			throw new UnableToUpdateNotificationException(message);
		}

		recipient.setReadState(read);

		try {
			persistenceService.persistOrUpdateNotification(notification);
		} catch (final PersistenceException e) {
			final String message = "Unable to update notification: Unable to persist it.";
			LOGGER.error(message, e);
			throw new UnableToUpdateNotificationException(message);
		}
	}

}
