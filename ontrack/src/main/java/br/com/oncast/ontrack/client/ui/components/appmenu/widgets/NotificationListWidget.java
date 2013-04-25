package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.notification.NotificationClientUtils;
import br.com.oncast.ontrack.client.services.notification.NotificationListChangeListener;
import br.com.oncast.ontrack.client.services.notification.NotificationReadStateUpdateCallback;
import br.com.oncast.ontrack.client.services.notification.NotificationService;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.shared.services.notification.Notification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class NotificationListWidget extends Composite implements HasCloseHandlers<NotificationListWidget>, PopupAware {

	private static final int NOTIFICATION_READ_STATE_DELAY_MILLIS = 1500;

	private static NotificationWidgetUiBinder uiBinder = GWT.create(NotificationWidgetUiBinder.class);

	private static final NotificationService NOTIFICATION_SERVICE = ClientServices.get().notifications();

	interface NotificationWidgetUiBinder extends UiBinder<Widget, NotificationListWidget> {}

	private final NotificationListChangeListener notificationListChangeListener;

	@UiField
	protected DeckPanel deckPanel;

	@UiField
	protected TabLayoutPanel container;

	@UiField
	protected DeckPanel notificationDeckPanel;

	@UiField
	protected DeckPanel activityDeckPanel;

	@UiField
	protected ScrollPanel notificationScrollContainer;

	@UiField
	protected ScrollPanel activityScrollContainer;

	@UiField(provided = true)
	protected ModelWidgetContainer<Notification, NotificationWidget> notificationContainer;

	@UiField(provided = true)
	protected ModelWidgetContainer<Notification, NotificationWidget> activityContainer;

	private final Timer timer = new Timer() {
		@Override
		public void run() {
			markVisibleNotificationsAsRead();
		}
	};

	public NotificationListWidget() {
		this.activityContainer = new ModelWidgetContainer<Notification, NotificationWidget>(new ModelWidgetFactory<Notification, NotificationWidget>() {
			@Override
			public NotificationWidget createWidget(final Notification modelBean) {
				return new NotificationWidget(modelBean);
			}
		});

		this.notificationContainer = new ModelWidgetContainer<Notification, NotificationWidget>(new ModelWidgetFactory<Notification, NotificationWidget>() {
			@Override
			public NotificationWidget createWidget(final Notification modelBean) {
				return new NotificationWidget(modelBean);
			}
		});

		initWidget(uiBinder.createAndBindUi(this));

		this.notificationListChangeListener = new NotificationListChangeListener() {

			@Override
			public void onNotificationListChanged(final List<Notification> activities) {
				final List<Notification> notifications = new ArrayList<Notification>();
				for (final Notification n : activities) {
					if (NOTIFICATION_SERVICE.isImportant(n)) notifications.add(n);
				}
				updateNotificationItens(notifications);
				updateActivitiesItens(activities);
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
		timer.cancel();
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

	private void updateActivitiesItens(final List<Notification> activities) {
		activityContainer.update(activities);
		activityDeckPanel.showWidget((activities.size() > 0) ? 1 : 0);
		activityScrollContainer.setVerticalScrollPosition(0);
	}

	private void scrollToLattestNotification() {
		notificationScrollContainer.setVerticalScrollPosition(0);
		timer.schedule(NOTIFICATION_READ_STATE_DELAY_MILLIS);
	}

	protected void hideLoadingIndicator() {
		deckPanel.showWidget(1);
		if (notificationContainer.getWidgetCount() == 0 && activityContainer.getWidgetCount() > 0) container.selectTab(1);
	}

	protected void showLoadingIndicator() {
		deckPanel.showWidget(0);
	}

	private void registerNotificationListChangeListener() {
		NOTIFICATION_SERVICE.registerNotificationListChangeListener(notificationListChangeListener);
	}

	private void unregisterNotificationListChangeListener() {
		NOTIFICATION_SERVICE.unregisterNotificationListChangeListener(notificationListChangeListener);
	}

	@UiHandler("notificationScrollContainer")
	protected void onScroll(final ScrollEvent event) {
		timer.schedule(NOTIFICATION_READ_STATE_DELAY_MILLIS);
	}

	private void markVisibleNotificationsAsRead() {
		if (!this.isVisible() || !this.isAttached()) return;
		final int widgetCount = notificationContainer.getWidgetCount();
		for (int i = 0; i < widgetCount; i++) {
			final NotificationWidget widget = notificationContainer.getWidget(i);
			final Element element = widget.getElement();

			final int listVisibleTop = notificationScrollContainer.getVerticalScrollPosition();
			final int listVisibleBottom = listVisibleTop + notificationScrollContainer.getElement().getClientHeight();
			final int elementTop = element.getOffsetTop();
			final int elementBottom = elementTop + element.getOffsetHeight();

			if (elementTop < listVisibleTop) continue;
			if (elementBottom > listVisibleBottom) break;
			if (NotificationClientUtils.getRecipientForCurrentUser(widget.getModelObject()).getReadState()) continue;

			final Notification notification = widget.getModelObject();
			NOTIFICATION_SERVICE.updateNotificationReadState(notification, true, new NotificationReadStateUpdateCallback() {

				@Override
				public void onError() {
					widget.updateReadState(false);
				}

				@Override
				public void notificationReadStateUpdated() {
					widget.updateReadState(true);
				}
			});
		}
	}
}
