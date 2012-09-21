package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.notification.NotificationListChangeListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.shared.services.notification.Notification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class NotificationListWidget extends Composite implements HasCloseHandlers<NotificationListWidget>, PopupAware {

	private static NotificationWidgetUiBinder uiBinder = GWT.create(NotificationWidgetUiBinder.class);

	private static ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	interface NotificationWidgetUiBinder extends UiBinder<Widget, NotificationListWidget> {}

	private final NotificationListChangeListener notificationListChangeListener;

	public NotificationListWidget() {
		initWidget(uiBinder.createAndBindUi(this));

		this.notificationListChangeListener = new NotificationListChangeListener() {

			@Override
			public void onNotificationListChanged(final Set<Notification> notifications) {
				updateNotificationItens(notifications);
			}

			@Override
			public void onNotificationListAvailabilityChange(final boolean availability) {
				if (availability) hideLoadingIndicator();
				else showLoadingIndicator();
			}
		};
		registerNotificationListChangeListener();
	}

	@Override
	public void show() {
		registerNotificationListChangeListener();
		scrollToLattestNotification();
	}

	@Override
	public void hide() {
		unregisterNotificationListChangeListener();
		if (!this.isVisible()) return;
		CloseEvent.fire(this, this);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<NotificationListWidget> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	private void updateNotificationItens(final Set<Notification> notifications) {
		// FIXME Notification
		// IF EMPTY SHOW EMPTY INDICATOR AND HIDE CONTAINER ELSE HIDE IT AND SHOW LIST
		// SET ITEMS IN CONTAINER
		scrollToLattestNotification();
	}

	private void scrollToLattestNotification() {
		// FIXME Notification
	}

	protected void hideLoadingIndicator() {
		// FIXME Notification
		// HIDE NOTIFICATION CONTAINER
		// SHOW LOADING INDICATOR
	}

	protected void showLoadingIndicator() {
		// FIXME Notification
		// HIDE LOADING INDICATOR
		// SHOW NOTIFICATION CONTAINER
	}

	private void registerNotificationListChangeListener() {
		SERVICE_PROVIDER.getNotificationService().registerNotificationListChangeListener(notificationListChangeListener);
	}

	private void unregisterNotificationListChangeListener() {
		SERVICE_PROVIDER.getNotificationService().unregisterNotificationListChangeListener(notificationListChangeListener);
	}
}
