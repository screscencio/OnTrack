package br.com.oncast.ontrack.shared.services.notification;

import br.com.oncast.ontrack.client.i18n.NotificationMessageCode;
import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.NotificationWidgetMessages;
import br.com.oncast.ontrack.client.utils.link.LinkFactory;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

public enum NotificationType implements NotificationMessageCode {
	IMPEDIMENT_CREATED() {
		@Override
		public String selectMessage(final NotificationWidgetMessages messages, final Notification notification) {
			return messages.impedimentCreatedNotificationWidgetMessage(getAnnotationLinkFor(notification), notification.getReferenceDescription(), getProjectLinkFor(messages, notification));
		}

		@Override
		public String simpleMessage(final Notification notification) {
			return "criou o impedimento";
		}
	},
	IMPEDIMENT_SOLVED() {
		@Override
		public String selectMessage(final NotificationWidgetMessages messages, final Notification notification) {
			return messages.impedimentSolvedNotificationWidgetMessage(getAnnotationLinkFor(notification), notification.getReferenceDescription(), getProjectLinkFor(messages, notification));
		}

		@Override
		public String simpleMessage(final Notification notification) {
			return "resolveu o impedimento";
		}
	},
	PROGRESS_DECLARED() {
		@Override
		public String selectMessage(final NotificationWidgetMessages messages, final Notification notification) {
			final String descriptionLink = getScopeLinkFor(notification);
			final String projectLink = getProjectLinkFor(messages, notification);

			final ProgressState progressState = ProgressState.getStateForDescription(notification.getDescription());
			if (progressState == ProgressState.DONE) return messages.progressDoneNotificationWidgetMessage(descriptionLink, projectLink);
			if (progressState == ProgressState.NOT_STARTED) return messages.progressNotStartedNotificationWidgetMessage(descriptionLink, projectLink);

			return messages.progressUnderworkNotificationWidgetMessage(notification.getDescription(), descriptionLink, projectLink);
		}

		@Override
		public String simpleMessage(final Notification notification) {
			// TODO NOT implemented
			return "";
		}
	},
	ANNOTATION_CREATED() {
		@Override
		public String selectMessage(final NotificationWidgetMessages messages, final Notification notification) {
			return messages.annotationCreatedNotificationWidgetMessage(getAnnotationLinkFor(notification), notification.getReferenceDescription(), getProjectLinkFor(messages, notification));
		}

		@Override
		public String simpleMessage(final Notification notification) {
			// TODO NOT implemented
			return "";
		}
	},
	ANNOTATION_DEPRECATED() {
		@Override
		public String selectMessage(final NotificationWidgetMessages messages, final Notification notification) {
			return messages.annotationDeprecatedNotificationWidgetMessage(getAnnotationLinkFor(notification), notification.getReferenceDescription(), getProjectLinkFor(messages, notification));
		}

		@Override
		public String simpleMessage(final Notification notification) {
			// TODO NOT implemented
			return "";
		}
	},
	TEAM_INVITED {
		@Override
		public String selectMessage(final NotificationWidgetMessages messages, final Notification notification) {
			return messages.teamInvitedNotificationWidgetMessage(notification.getReferenceDescription());
		}

		@Override
		public String simpleMessage(final Notification notification) {
			return "convidou o " + notification.getReferenceDescription() + " para o projeto";
		}
	},
	TEAM_REMOVED {
		@Override
		public String selectMessage(final NotificationWidgetMessages messages, final Notification notification) {
			return messages.teamRemovedNotificationWidgetMessage(notification.getReferenceDescription());
		}

		@Override
		public String simpleMessage(final Notification notification) {
			return "excluiu o " + notification.getReferenceDescription() + " do projeto";
		}
	};

	@Override
	public abstract String selectMessage(final NotificationWidgetMessages messages, final Notification notification);

	public abstract String simpleMessage(final Notification notification);

	private static String getProjectLinkFor(final NotificationWidgetMessages messages, final Notification notification) {
		try {
			final ProjectRepresentation project = ClientServices.get().projectRepresentationProvider().getProjectRepresentation(notification.getProjectId());
			return LinkFactory.getLinkForProject(project).asString();
		} catch (final RuntimeException e) {
			return messages.unavailableProject();
		}

	}

	private static String getAnnotationLinkFor(final Notification notification) {
		return LinkFactory.getLinkForAnnotation(notification.getProjectId(), notification.getReferenceId(), notification.getDescription()).asString();
	}

	private static String getScopeLinkFor(final Notification notification) {
		return LinkFactory.getScopeLinkFor(notification.getProjectId(), notification.getReferenceId(), notification.getReferenceDescription()).asString();
	}
}
