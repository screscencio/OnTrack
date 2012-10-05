package br.com.oncast.ontrack.server.business.actionPostProcessments;

import java.util.List;

import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.notification.NotificationServerService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateNotificationException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ImpedimentCreateAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.Notification.NotificationType;
import br.com.oncast.ontrack.shared.services.notification.NotificationBuilder;

public class ImpedimentNotificationCreationPostProcessor implements ActionPostProcessor<ImpedimentCreateAction> {

	private final NotificationServerService notificationServerService;
	private final PersistenceService persistenceService;

	public ImpedimentNotificationCreationPostProcessor(final NotificationServerService notificationServerService, final PersistenceService persistenceService) {
		this.notificationServerService = notificationServerService;
		this.persistenceService = persistenceService;
	}

	@Override
	public void process(final ImpedimentCreateAction action, final ActionContext actionContext, final ProjectContext projectContext)
			throws UnableToPostProcessActionException {
		try {
			final Notification notification = createNofification(action, actionContext, projectContext);
			if (notification == null) return;

			this.notificationServerService.registerNewNotification(notification);
		}
		catch (final UnableToCreateNotificationException e) {
			throw new UnableToPostProcessActionException("It was not possible to register new notification.", e);
		}
		catch (final PersistenceException e) {
			throw new UnableToPostProcessActionException("It was not possible to create new notification: Unable to retrieve project user list.", e);
		}
	}

	private Notification createNofification(final ImpedimentCreateAction action, final ActionContext actionContext, final ProjectContext projectContext)
			throws PersistenceException {
		try {
			final ProjectRepresentation projectRepresentation = projectContext.getProjectRepresentation();
			final List<User> projectUsers = persistenceService.retrieveProjectUsers(projectRepresentation);
			final User author = persistenceService.retrieveUserByEmail(actionContext.getUserEmail());

			final NotificationBuilder notificationBuilder = new NotificationBuilder(NotificationType.IMPEDIMENT, projectRepresentation, author);
			notificationBuilder.setReferenceId(action.getReferenceId());

			for (final User user : projectUsers) {
				notificationBuilder.addReceipient(user);
			}

			return notificationBuilder.getNotification();
		}
		catch (final NoResultFoundException e) {
			throw new UnableToPostProcessActionException("The author user '" + actionContext.getUserEmail() + "' could not be retrieved from the persistence.",
					e);
		}
	}
}
