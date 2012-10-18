package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.notification.NotificationClientUtils;
import br.com.oncast.ontrack.client.services.notification.NotificationListChangeListener;
import br.com.oncast.ontrack.client.services.notification.NotificationReadStateChangeListener;
import br.com.oncast.ontrack.client.services.notification.NotificationService;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.shared.services.notification.Notification;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class NotificationMenuItem extends Composite implements IsWidget, HasText {

	private final class NotificationMenuListenerImplementation implements NotificationListChangeListener, NotificationReadStateChangeListener {
		private List<Notification> notifications;

		@Override
		public void onNotificationListChanged(final List<Notification> notifications) {
			this.notifications = notifications;
			calculateUnreadNotifications();
		}

		@Override
		public void onNotificationListAvailabilityChange(final boolean availability) {
			if (!availability) setSuffix("(?)");
			else {
				calculateUnreadNotifications();
			}
		}

		private void calculateUnreadNotifications() {
			final List<Notification> unread = NotificationClientUtils.getUnreadNotificationsForCurrentUser(notifications);
			if (unread.size() > 0) setSuffix("(" + unread.size() + ")");
			else setSuffix("");
		}

		@Override
		public void readStateChanged(final Notification notification, final boolean readState) {
			calculateUnreadNotifications();
		}
	}

	protected ApplicationMenuItem notificationMenuItem;
	protected String suffix = "";
	private String text = "";

	public NotificationMenuItem() {
		initWidget(notificationMenuItem = new ApplicationMenuItem());
		final NotificationService notificationService = ClientServiceProvider.getInstance().getNotificationService();
		final NotificationMenuListenerImplementation listener = new NotificationMenuListenerImplementation();
		notificationService.registerNotificationListChangeListener(listener);
		notificationService.registerNotificationReadStateChangeListener(listener);
	}

	@Override
	public Widget asWidget() {
		return notificationMenuItem;
	}

	@Override
	public String getText() {
		return notificationMenuItem.getText();
	}

	@Override
	public void setText(final String text) {
		this.text = text;
		updateText(text);
	}

	protected void setSuffix(final String suffix) {
		this.suffix = suffix;
		updateText(text);
	}

	private void updateText(final String text) {
		notificationMenuItem.setText(text + " " + suffix);
	}

	public void setPopupConfig(final PopupConfig popup) {
		notificationMenuItem.setPopupConfig(popup);
	}
}
