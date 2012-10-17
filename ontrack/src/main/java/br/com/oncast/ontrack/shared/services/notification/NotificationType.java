package br.com.oncast.ontrack.shared.services.notification;

import br.com.oncast.ontrack.client.i18n.NotificationMessageCode;
import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.NotificationWidgetMessages;
import br.com.oncast.ontrack.client.utils.link.LinkFactory;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

public enum NotificationType implements NotificationMessageCode {
	IMPEDIMENT_CREATED() {
		@Override
		public String selectMessage(final NotificationWidgetMessages messages, final Notification notification) {
			return messages.impedimentCreatedNotificationWidgetMessage(getAnnotationLinkFor(notification),
					notification.getReferenceDescription(),
					getProjectLinkFor(notification));
		}
	},
	IMPEDIMENT_SOLVED() {
		@Override
		public String selectMessage(final NotificationWidgetMessages messages, final Notification notification) {
			return messages.impedimentSolvedNotificationWidgetMessage(getAnnotationLinkFor(notification),
					notification.getReferenceDescription(),
					getProjectLinkFor(notification));
		}
	},
	PROGRESS_DECLARED() {
		@Override
		public String selectMessage(final NotificationWidgetMessages messages, final Notification notification) {
			final String descriptionLink = getScopeLinkFor(notification);
			final String projectLink = getProjectLinkFor(notification);

			final ProgressState progressState = ProgressState.getStateForDescription(notification.getDescription());
			if (progressState == ProgressState.DONE) return messages.progressDoneNotificationWidgetMessage(descriptionLink, projectLink);
			if (progressState == ProgressState.NOT_STARTED) return messages.progressNotStartedNotificationWidgetMessage(descriptionLink, projectLink);

			return messages.progressUnderworkNotificationWidgetMessage(notification.getDescription(), descriptionLink, projectLink);
		}
	},
	ANNOTATION_CREATED() {
		@Override
		public String selectMessage(final NotificationWidgetMessages messages, final Notification notification) {
			return messages.annotationCreatedNotificationWidgetMessage(getAnnotationLinkFor(notification),
					notification.getReferenceDescription(),
					getProjectLinkFor(notification));
		}
	};

	@Override
	public abstract String selectMessage(final NotificationWidgetMessages messages, final Notification notification);

	private static String getProjectLinkFor(final Notification notification) {
		final ProjectRepresentation project = ClientServiceProvider.getInstance().getProjectRepresentationProvider()
				.getProjectRepresentation(notification.getProjectId());

		return LinkFactory.getLinkForProject(project).asString();
	}

	private static String getAnnotationLinkFor(final Notification notification) {
		return LinkFactory.getLinkForAnnotation(notification.getProjectId(), notification.getReferenceId(), notification.getDescription()).asString();
	}

	private static String getScopeLinkFor(final Notification notification) {
		return LinkFactory.getScopeLinkFor(notification.getProjectId(), notification.getReferenceId(), notification.getReferenceDescription()).asString();
	}
}
