package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.notification.NotificationClientUtils;
import br.com.oncast.ontrack.client.services.notification.NotificationListChangeListener;
import br.com.oncast.ontrack.client.services.notification.NotificationReadStateChangeListener;
import br.com.oncast.ontrack.client.services.notification.NotificationService;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.shared.services.notification.Notification;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
			if (!availability) setSuffix("?");
			else {
				calculateUnreadNotifications();
			}
		}

		private void calculateUnreadNotifications() {
			final List<Notification> unreadNotificationsForCurrentUser = NotificationClientUtils.getUnreadNotificationsForCurrentUser(notifications);
			setSuffix("" + unreadNotificationsForCurrentUser.size());
			notificationMenuItemHeader.setHasUnread(!unreadNotificationsForCurrentUser.isEmpty());
		}

		@Override
		public void readStateChanged(final Notification notification, final boolean readState) {
			calculateUnreadNotifications();
		}
	}

	private final NotificationMenuItemHeaderWidget notificationMenuItemHeader;

	private PopupConfig popup;

	public NotificationMenuItem() {
		initWidget(notificationMenuItemHeader = new NotificationMenuItemHeaderWidget());
		final NotificationService notificationService = ClientServices.get().notifications();
		final NotificationMenuListenerImplementation listener = new NotificationMenuListenerImplementation();
		notificationService.registerNotificationListChangeListener(listener);
		notificationService.registerNotificationReadStateChangeListener(listener);
		notificationMenuItemHeader.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				openMenu();
			}
		});
	}

	@Override
	public Widget asWidget() {
		return notificationMenuItemHeader;
	}

	protected void setSuffix(final String string) {
		notificationMenuItemHeader.setNotificationCountIndicator(string);
	}

	public void setPopupConfig(final PopupConfig popup) {
		this.popup = popup;
		popup.setAnimationDuration(PopupConfig.SlideAnimation.DURATION_SHORT);
	}

	public void openMenu() {
		popup.pop();
	}

	@Override
	public String getText() {
		return notificationMenuItemHeader.getText();
	}

	@Override
	public void setText(final String text) {
		notificationMenuItemHeader.setText(text);
	}
}
