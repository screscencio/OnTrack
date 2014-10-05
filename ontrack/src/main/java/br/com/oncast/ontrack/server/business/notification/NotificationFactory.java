package br.com.oncast.ontrack.server.business.notification;

import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.annotation.exceptions.AnnotationNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.NotificationBuilder;

public class NotificationFactory {

	public Notification createNofification(final ModelAction action, final ActionContext actionContext, final ProjectContext projectContext) throws NoResultFoundException, PersistenceException,
			AnnotationNotFoundException, ScopeNotFoundException {
		final ActionNotificationCreator creator = ActionNotificationCreator.getCreatorFor(action.getClass());
		if (creator == null) return null;

		final NotificationBuilder notificationBuilder = creator.createNotificationBuilder(action, projectContext, actionContext.getUserId());

		if (notificationBuilder == null) return null;

		for (final UserRepresentation user : projectContext.getUsers()) {
			if (user.isValid()) notificationBuilder.addReceipient(user.getId());
		}

		return notificationBuilder.getNotification();
	}
}
