package br.com.oncast.ontrack.server.business.notification;

import java.util.List;

import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentCreateAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentSolveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.exceptions.AnnotationNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.shared.services.notification.NotificationBuilder;
import br.com.oncast.ontrack.shared.services.notification.NotificationType;

public class NotificationFactory {

	private enum ActionNotificationCreator {
		IMPEDIMENT_CREATION(ImpedimentCreateAction.class) {
			@Override
			protected NotificationBuilder createNotificationBuilder(final ModelAction action, final ProjectContext projectContext,
					final User author) {

				final String referenceDescription = getReferenceDescription(action, projectContext);
				final Annotation annotation = getAnnotationById(projectContext, action.getReferenceId(), ((ImpedimentCreateAction) action).getAnnotationId());

				return initializeBuilder(action, projectContext.getProjectRepresentation(), author, NotificationType.IMPEDIMENT_CREATED)
						.setDescription(annotation.getMessage())
						.setReferenceDescription(referenceDescription);
			}
		},
		IMPEDIMENT_COMPLETITION(ImpedimentSolveAction.class) {
			@Override
			protected NotificationBuilder createNotificationBuilder(final ModelAction action, final ProjectContext projectContext,
					final User author) {

				final String referenceDescription = getReferenceDescription(action, projectContext);
				final Annotation annotation = getAnnotationById(projectContext, action.getReferenceId(), ((ImpedimentSolveAction) action).getAnnotationId());

				return initializeBuilder(action, projectContext.getProjectRepresentation(), author, NotificationType.IMPEDIMENT_SOLVED)
						.setDescription(annotation.getMessage())
						.setReferenceDescription(referenceDescription);
			}
		},
		PROGRESS_DECLARED(ScopeDeclareProgressAction.class) {
			@Override
			protected NotificationBuilder createNotificationBuilder(final ModelAction action, final ProjectContext projectContext,
					final User author) {

				final Scope scope = getScopeById(action, projectContext);
				return initializeBuilder(action, projectContext.getProjectRepresentation(), author, NotificationType.PROGRESS_DECLARED)
						.setReferenceDescription(scope.getDescription())
						.setDescription(scope.getProgress().getDescription());
			}
		},
		ANNOTATION_CREATED(AnnotationCreateAction.class) {
			@Override
			protected NotificationBuilder createNotificationBuilder(final ModelAction action, final ProjectContext projectContext,
					final User author) {

				final String referenceDescription = getReferenceDescription(action, projectContext);
				final Annotation annotation = getAnnotationById(projectContext, action.getReferenceId(), ((AnnotationCreateAction) action).getAnnotationId());

				return initializeBuilder(action, projectContext.getProjectRepresentation(), author, NotificationType.ANNOTATION_CREATED)
						.setDescription(annotation.getMessage())
						.setReferenceDescription(referenceDescription);
			}
		};

		private static NotificationBuilder initializeBuilder(final ModelAction action, final ProjectRepresentation projectRepresentation,
				final User author, final NotificationType type) {

			return new NotificationBuilder(type, projectRepresentation, author).setReferenceId(action.getReferenceId());
		}

		private static Scope getScopeById(final ModelAction action, final ProjectContext projectContext) {
			try {
				return projectContext.findScope(action.getReferenceId());
			}
			catch (final ScopeNotFoundException e3) {
				throw new UnableToPostProcessActionException("It was not possible to create new notification builder.", e3);
			}
		}

		private static String getReferenceDescription(final ModelAction action, final ProjectContext projectContext) {
			try {
				return projectContext.findScope(action.getReferenceId()).getDescription();
			}
			catch (final ScopeNotFoundException e3) {
				try {
					return projectContext.findRelease(action.getReferenceId()).getDescription();
				}
				catch (final ReleaseNotFoundException e) {
					throw new UnableToPostProcessActionException("It was not possible to create new notification builder.", e3);
				}
			}
		}

		private static Annotation getAnnotationById(final ProjectContext projectContext, final UUID referenceId, final UUID annotationId) {
			try {
				return projectContext.findAnnotation(referenceId, annotationId);
			}
			catch (final AnnotationNotFoundException e) {
				throw new UnableToPostProcessActionException("It was not possible to create new notification builder.", e);
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

		protected abstract NotificationBuilder createNotificationBuilder(ModelAction action, ProjectContext projectContext, User author);
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

			final NotificationBuilder notificationBuilder = creator.createNotificationBuilder(action, projectContext, author);

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
