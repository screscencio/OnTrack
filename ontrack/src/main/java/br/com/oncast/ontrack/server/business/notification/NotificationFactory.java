package br.com.oncast.ontrack.server.business.notification;

import java.util.List;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ImpedimentCreateAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentSolveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.Notification.NotificationType;
import br.com.oncast.ontrack.shared.services.notification.NotificationBuilder;

public class NotificationFactory {

	private enum ActionNotificationCreator {
		IMPEDIMENT_CREATION(ImpedimentCreateAction.class) {
			@Override
			protected NotificationBuilder createNotificationBuilder(final ModelAction action, final ProjectRepresentation projectRepresentation,
					final User author) {

				return initializeBuilder(action, projectRepresentation, author, NotificationType.IMPEDIMENT_CREATED);
			}
		},
		IMPEDIMENT_COMPLETITION(ImpedimentSolveAction.class) {
			@Override
			protected NotificationBuilder createNotificationBuilder(final ModelAction action, final ProjectRepresentation projectRepresentation,
					final User author) {

				return initializeBuilder(action, projectRepresentation, author, NotificationType.IMPEDIMENT_SOLVED);
			}
		},
		PROGRESS_DECLARED(ScopeDeclareProgressAction.class) {
			@Override
			protected NotificationBuilder createNotificationBuilder(final ModelAction action, final ProjectRepresentation projectRepresentation,
					final User author) {

				return initializeBuilder(action, projectRepresentation, author, NotificationType.PROGRESS_DECLARED).setDescription(
						getScopeDescriptionFor(action, projectRepresentation));
			}

		};

		private static NotificationBuilder initializeBuilder(final ModelAction action, final ProjectRepresentation projectRepresentation,
				final User author, final NotificationType type) {

			return new NotificationBuilder(type, projectRepresentation, author).setReferenceId(action.getReferenceId());
		}

		private static String getScopeDescriptionFor(final ModelAction action, final ProjectRepresentation projectRepresentation) {
			try {
				final Project project = ServerServiceProvider.getInstance().getBusinessLogic().loadProject(projectRepresentation.getId());

				return new ProjectContext(project).findScope(action.getReferenceId()).getDescription();
			}
			catch (final ProjectNotFoundException e1) {
				throw new UnableToPostProcessActionException("It was not possible to create new notification builder.", e1);
			}
			catch (final ScopeNotFoundException e2) {
				throw new UnableToPostProcessActionException("It was not possible to create new notification builder.", e2);
			}
			catch (final UnableToLoadProjectException e3) {
				throw new UnableToPostProcessActionException("It was not possible to create new notification builder.", e3);
			}
		}

		private final Class<? extends ModelAction> clazz;

		private ActionNotificationCreator(final Class<? extends ModelAction> clazz) {
			this.clazz = clazz;
		}

		protected static ActionNotificationCreator getCreatorFor(final Class<? extends ModelAction> clazz) {
			final ActionNotificationCreator[] values = ActionNotificationCreator.values();
			for (final ActionNotificationCreator actionNotificationCreator : values) {
				if (actionNotificationCreator.clazz.isAssignableFrom(clazz)) return actionNotificationCreator;
			}
			return null;
		}

		protected abstract NotificationBuilder createNotificationBuilder(ModelAction action, ProjectRepresentation projectRepresentation, User author);
	}

	private final PersistenceService persistenceService;

	public NotificationFactory(final PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	public Notification createNofification(final ModelAction action, final ActionContext actionContext, final ProjectContext projectContext)
			throws PersistenceException {
		try {
			final ActionNotificationCreator creator = ActionNotificationCreator.getCreatorFor(action.getClass());
			if (creator == null) return null;

			final ProjectRepresentation projectRepresentation = projectContext.getProjectRepresentation();
			final List<User> projectUsers = persistenceService.retrieveProjectUsers(projectRepresentation);
			final User author = persistenceService.retrieveUserByEmail(actionContext.getUserEmail());

			final NotificationBuilder notificationBuilder = creator.createNotificationBuilder(action, projectRepresentation, author);

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
