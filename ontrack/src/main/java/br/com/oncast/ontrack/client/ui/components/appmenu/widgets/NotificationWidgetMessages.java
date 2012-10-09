package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface NotificationWidgetMessages extends BaseMessages {

	@Description("no notifications in project message")
	@DefaultMessage("No Notification yet")
	String noNotifications();

	@Description("notification help message")
	@DefaultMessage("Notifications will arise when relevant events happen")
	String noNotificationsHelpMessage();

	@Description("impediment created")
	@DefaultMessage("Impediment created")
	String impedimentCreatedNotificationMessage();

	@Description("impediment solved")
	@DefaultMessage("Impediment solved")
	String impedimentSolvedNotificationMessage();

	@Description("progress declared")
	@DefaultMessage("Progress declared")
	String progressDeclaredNotificationMessage();

	@Description("annotation created notification")
	@DefaultMessage("Annotation created")
	String annotationCreatedNotificationMessage();

}
