package br.com.oncast.ontrack.server.business.notification;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToPostProcessActionException;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationDeprecateAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentCreateAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentSolveAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.action.TeamRevogueInvitationAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.annotation.exceptions.AnnotationNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.notification.NotificationBuilder;
import br.com.oncast.ontrack.shared.services.notification.NotificationType;

public enum ActionNotificationCreator {

	TEAM_INVITE(TeamInviteAction.class) {
		@Override
		protected NotificationBuilder createNotificationBuilder(final ModelAction action, final ProjectContext projectContext, final UUID authorId) throws NoResultFoundException, PersistenceException {
			final NotificationBuilder builder = initializeBuilder(action, projectContext.getProjectRepresentation(), authorId, NotificationType.TEAM_INVITED);
			final User user = ServerServiceProvider.getInstance().getUsersDataManager().retrieveUser(action.getReferenceId());
			builder.setReferenceDescription(user.getName());
			return builder;
		}
	},
	TEAM_REVOGUE_INVITATION(TeamRevogueInvitationAction.class) {
		@Override
		protected NotificationBuilder createNotificationBuilder(final ModelAction action, final ProjectContext projectContext, final UUID authorId) throws NoResultFoundException, PersistenceException {
			final NotificationBuilder builder = initializeBuilder(action, projectContext.getProjectRepresentation(), authorId, NotificationType.TEAM_REMOVED);
			final User user = ServerServiceProvider.getInstance().getUsersDataManager().retrieveUser(action.getReferenceId());
			builder.setReferenceDescription(user.getName());
			return builder;
		}
	},
	IMPEDIMENT_CREATION(ImpedimentCreateAction.class) {
		@Override
		protected NotificationBuilder createNotificationBuilder(final ModelAction action, final ProjectContext projectContext, final UUID authorId) {
			return createAnnotationNotificationBuilder(action, projectContext, authorId, ((ImpedimentCreateAction) action).getAnnotationId(), NotificationType.IMPEDIMENT_CREATED);
		}
	},
	IMPEDIMENT_COMPLETITION(ImpedimentSolveAction.class) {
		@Override
		protected NotificationBuilder createNotificationBuilder(final ModelAction action, final ProjectContext projectContext, final UUID authorId) {
			return createAnnotationNotificationBuilder(action, projectContext, authorId, ((ImpedimentSolveAction) action).getAnnotationId(), NotificationType.IMPEDIMENT_SOLVED);
		}
	},
	PROGRESS_DECLARED(ScopeDeclareProgressAction.class) {
		@Override
		protected NotificationBuilder createNotificationBuilder(final ModelAction action, final ProjectContext projectContext, final UUID authorId) {
			final Scope scope = getScopeById(action, projectContext);
			return initializeBuilder(action, projectContext.getProjectRepresentation(), authorId, NotificationType.PROGRESS_DECLARED).setReferenceDescription(scope.getDescription()).setDescription(
					scope.getProgress().getDescription());
		}
	},
	ANNOTATION_CREATED(AnnotationCreateAction.class) {
		@Override
		protected NotificationBuilder createNotificationBuilder(final ModelAction action, final ProjectContext projectContext, final UUID authorId) {
			final AnnotationCreateAction annotationAction = (AnnotationCreateAction) action;
			final NotificationType notificationType = annotationAction.getAnnotationType() == AnnotationType.SIMPLE ? NotificationType.ANNOTATION_CREATED : NotificationType.IMPEDIMENT_CREATED;
			return createAnnotationNotificationBuilder(action, projectContext, authorId, annotationAction.getAnnotationId(), notificationType);
		}
	},
	ANNOTATION_DEPRECATED(AnnotationDeprecateAction.class) {
		@Override
		protected NotificationBuilder createNotificationBuilder(final ModelAction action, final ProjectContext projectContext, final UUID authorId) {
			return createAnnotationNotificationBuilder(action, projectContext, authorId, ((AnnotationDeprecateAction) action).getAnnotationId(), NotificationType.ANNOTATION_DEPRECATED);
		}
	};

	private static NotificationBuilder createAnnotationNotificationBuilder(final ModelAction action, final ProjectContext projectContext, final UUID authorId, final UUID annotationId,
			final NotificationType type) {
		String referenceDescription = "";
		try {
			referenceDescription = getReferenceDescription(action, projectContext);
		} catch (final RuntimeException e) {
			return null;
		}
		final Annotation annotation = getAnnotationById(projectContext, action.getReferenceId(), annotationId);

		return initializeBuilder(action, projectContext.getProjectRepresentation(), authorId, type).setDescription(annotation.getMessage()).setReferenceDescription(referenceDescription);
	}

	private static NotificationBuilder initializeBuilder(final ModelAction action, final ProjectRepresentation projectRepresentation, final UUID authorId, final NotificationType type) {

		return new NotificationBuilder(type, projectRepresentation, authorId).setReferenceId(action.getReferenceId());
	}

	private static Scope getScopeById(final ModelAction action, final ProjectContext projectContext) {
		try {
			return projectContext.findScope(action.getReferenceId());
		} catch (final ScopeNotFoundException e3) {
			throw new UnableToPostProcessActionException("It was not possible to create new notification builder.", e3);
		}
	}

	private static String getReferenceDescription(final ModelAction action, final ProjectContext projectContext) {
		try {
			return projectContext.findScope(action.getReferenceId()).getDescription();
		} catch (final ScopeNotFoundException e3) {
			try {
				return projectContext.findRelease(action.getReferenceId()).getDescription();
			} catch (final ReleaseNotFoundException e) {
				throw new RuntimeException("Description not found.", e3);
			}
		}
	}

	private static Annotation getAnnotationById(final ProjectContext projectContext, final UUID referenceId, final UUID annotationId) {
		try {
			return projectContext.findAnnotation(referenceId, annotationId);
		} catch (final AnnotationNotFoundException e) {
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

	protected abstract NotificationBuilder createNotificationBuilder(ModelAction action, ProjectContext projectContext, UUID authorId) throws NoResultFoundException, PersistenceException;

}
