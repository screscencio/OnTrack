package br.com.oncast.ontrack.server.business.actionPostProcessments;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.business.notification.NotificationFactory;
import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.notification.NotificationServerService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateNotificationException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.services.notification.Notification;

public class NotificationCreationPostProcessor implements ActionPostProcessor<ModelAction> {

	private static final Logger LOGGER = Logger.getLogger(NotificationCreationPostProcessor.class);
	private final NotificationServerService notificationServerService;
	private final NotificationFactory notificationFactory;
	private boolean active;

	public NotificationCreationPostProcessor(final NotificationServerService notificationServerService, final PersistenceService persistenceService) {
		this.notificationServerService = notificationServerService;
		this.notificationFactory = new NotificationFactory();
		active = true;
	}

	@Override
	public void process(final ModelAction action, final ActionContext actionContext, final ProjectContext projectContext)
			throws UnableToPostProcessActionException {
		if (!active) {
			LOGGER.info("Ignoring notification post processment of action '" + action + "': the post processor was deactivated.");
			return;
		}

		try {
			final Notification notification = notificationFactory.createNofification(action, actionContext, projectContext);
			if (notification == null) return;

			this.notificationServerService.registerNewNotification(notification);
		}
		catch (final UnableToCreateNotificationException e) {
			throw new UnableToPostProcessActionException("It was not possible to register new notification.", e);
		}
	}

	public void deactivate() {
		LOGGER.warn("Deactivating notification post processment.");
		active = false;
	}

	public void activate() {
		LOGGER.warn("Activating notification post processment.");
		active = true;
	}
}
