package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface NotificationWidgetMessages extends BaseMessages {

	@Description("no notifications in project message")
	@DefaultMessage("No Notification yet")
	String noNotifications();

	@Description("notification help message")
	@DefaultMessage("Notifications will arise when relevant events happen")
	String noNotificationsHelpMessage();

	@Description("annotation created notification widget")
	@DefaultMessage("created an annotation ''{0}'' on item ''{1}'' at ''{2}''")
	String annotationCreatedNotificationWidgetMessage(String annotationDescription, String scopeDescription, String project);

	@Description("impediment created notification widget")
	@DefaultMessage("created the impediment ''{0}'' on item ''{1}'' at ''{2}''")
	String impedimentCreatedNotificationWidgetMessage(String annotationDescription, String scopeDescription, String project);

	@Description("impediment solved notification widget")
	@DefaultMessage("solved the impediment ''{0}'' on item ''{1}'' at ''{2}''")
	String impedimentSolvedNotificationWidgetMessage(String annotationDescription, String scopeDescription, String project);

	@Description("progress not started notification widget")
	@DefaultMessage("marked as not started the item ''{0}'' at ''{1}''")
	String progressNotStartedNotificationWidgetMessage(String scopeDescription, String project);

	@Description("progress underwork notification widget")
	@DefaultMessage("marked as ''{0}'' the item ''{1}'' at ''{2}''")
	String progressUnderworkNotificationWidgetMessage(String progressDescription, String scopeDescription, String project);

	@Description("progress done notification widget")
	@DefaultMessage("marked as done the item ''{0}'' at ''{1}''")
	String progressDoneNotificationWidgetMessage(String scopeDescription, String project);

	@Description("annotation deprecated notification widget")
	@DefaultMessage("deprecated an annotation ''{0}'' on item ''{1}'' at ''{2}''")
	String annotationDeprecatedNotificationWidgetMessage(String annotationLinkFor, String referenceDescription, String projectLinkFor);

}
