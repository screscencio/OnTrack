package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.notification.NotificationListChangeListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.generalwidgets.VerticalModelWidgetContainer;
import br.com.oncast.ontrack.shared.services.notification.Notification;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Widget;

// FIXME Notification MAKE THE PANEL SCROLLABLE WITH MAX HEIGHT
public class NotificationListWidget extends Composite implements HasCloseHandlers<NotificationListWidget>, PopupAware {

	private static NotificationWidgetUiBinder uiBinder = GWT.create(NotificationWidgetUiBinder.class);

	private static ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	interface NotificationWidgetUiBinder extends UiBinder<Widget, NotificationListWidget> {}

	private final NotificationListChangeListener notificationListChangeListener;

	@UiField
	@IgnoredByDeepEquality
	protected DeckPanel deckPanel;

	@UiField
	@IgnoredByDeepEquality
	protected DeckPanel notificationDeckPanel;

	@UiField
	@IgnoredByDeepEquality
	protected VerticalModelWidgetContainer<Notification, NotificationWidget> notificationContainer;

	@UiFactory
	protected VerticalModelWidgetContainer<Notification, NotificationWidget> createNotificationContainer() {
		return new VerticalModelWidgetContainer<Notification, NotificationWidget>(new ModelWidgetFactory<Notification, NotificationWidget>() {

			@Override
			public NotificationWidget createWidget(final Notification modelBean) {
				return new NotificationWidget(modelBean);
			}
		}, new ModelWidgetContainerListener() {

			@Override
			public void onUpdateComplete(final boolean hasChanged) {}
		});
	}

	public NotificationListWidget() {
		initWidget(uiBinder.createAndBindUi(this));

		this.notificationListChangeListener = new NotificationListChangeListener() {

			@Override
			public void onNotificationListChanged(final List<Notification> notifications) {
				updateNotificationItens(notifications);
			}

			@Override
			public void onNotificationListAvailabilityChange(final boolean availability) {
				if (availability) hideLoadingIndicator();
				else showLoadingIndicator();
			}
		};
		showLoadingIndicator();
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

	private void updateNotificationItens(final List<Notification> notifications) {
		notificationContainer.update(notifications);
		notificationDeckPanel.showWidget((notifications.size() > 0) ? 1 : 0);
		scrollToLattestNotification();
	}

	private void scrollToLattestNotification() {
		// FIXME Notification SCROLL TO THE LATTEST UNSEEN NOTIFICATION
	}

	protected void hideLoadingIndicator() {
		deckPanel.showWidget(1);
	}

	protected void showLoadingIndicator() {
		deckPanel.showWidget(0);
	}

	private void registerNotificationListChangeListener() {
		SERVICE_PROVIDER.getNotificationService().registerNotificationListChangeListener(notificationListChangeListener);
	}

	private void unregisterNotificationListChangeListener() {
		SERVICE_PROVIDER.getNotificationService().unregisterNotificationListChangeListener(notificationListChangeListener);
	}
}
