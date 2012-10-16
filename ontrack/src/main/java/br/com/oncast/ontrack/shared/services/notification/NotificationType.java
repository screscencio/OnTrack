package br.com.oncast.ontrack.shared.services.notification;

import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.NotificationWidgetMessages;
import br.com.oncast.ontrack.shared.messageCode.BaseMessageCode;

public enum NotificationType implements BaseMessageCode<NotificationWidgetMessages> {

	IMPEDIMENT_CREATED() {
		@Override
		public String selectMessage(final NotificationWidgetMessages messages, final String... args) {
			return messages.impedimentCreatedNotificationMessage();
		}
	},
	IMPEDIMENT_SOLVED() {
		@Override
		public String selectMessage(final NotificationWidgetMessages messages, final String... args) {
			return messages.impedimentSolvedNotificationMessage();
		}
	},
	PROGRESS_DECLARED() {
		@Override
		public String selectMessage(final NotificationWidgetMessages messages, final String... args) {
			return messages.progressDeclaredNotificationMessage();
		}
	},
	ANNOTATION_CREATED() {
		@Override
		public String selectMessage(final NotificationWidgetMessages messages, final String... args) {
			return messages.annotationCreatedNotificationMessage();
		}
	};

	@Override
	public abstract String selectMessage(final NotificationWidgetMessages messages, final String... args);
}
