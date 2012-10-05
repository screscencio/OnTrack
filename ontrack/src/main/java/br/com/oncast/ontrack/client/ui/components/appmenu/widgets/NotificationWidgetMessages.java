package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface NotificationWidgetMessages extends BaseMessages {

	@Description("no notifications in project message")
	@DefaultMessage("No Notification yet")
	String noNotifications();

	@Description("notification help message")
	@DefaultMessage("Notifications will arise when relevant events happen")
	String noNotificationsHelpMessage();

	@Description("impediment")
	@DefaultMessage("Impediment")
	String impedimentNotificationMessage();

}
