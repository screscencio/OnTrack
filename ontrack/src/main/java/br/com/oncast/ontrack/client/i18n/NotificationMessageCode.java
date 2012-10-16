package br.com.oncast.ontrack.client.i18n;

import br.com.oncast.ontrack.client.ui.components.appmenu.widgets.NotificationWidgetMessages;
import br.com.oncast.ontrack.shared.services.notification.Notification;

public interface NotificationMessageCode {

	String selectMessage(final NotificationWidgetMessages messages, final Notification notification);

}
