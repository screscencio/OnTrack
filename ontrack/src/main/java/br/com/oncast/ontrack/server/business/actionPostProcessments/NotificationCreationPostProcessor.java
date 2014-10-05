package br.com.oncast.ontrack.server.business.actionPostProcessments;

import br.com.oncast.ontrack.server.business.notification.NotificationFactory;
import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.notification.NotificationServerService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateNotificationException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.annotation.exceptions.AnnotationNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.services.notification.Notification;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

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
	public void process(final ModelAction action, final ActionContext actionContext, final ProjectContext projectContext) throws UnableToPostProcessActionException, NoResultFoundException,
			PersistenceException, AnnotationNotFoundException, ScopeNotFoundException, UnableToCreateNotificationException, MessagingException {
		if (!active) {
			LOGGER.debug("Ignoring notification post processment of action '" + action + "': the post processor was deactivated.");
			return;
		}

		final Notification notification = notificationFactory.createNofification(action, actionContext, projectContext);
		if (notification == null) return;
		this.notificationServerService.registerNewNotification(notification);

	}

	public void deactivate() {
		LOGGER.debug("Deactivating notification post processment.");
		active = false;
	}

	public void activate() {
		LOGGER.debug("Activating notification post processment.");
		active = true;
	}
}
