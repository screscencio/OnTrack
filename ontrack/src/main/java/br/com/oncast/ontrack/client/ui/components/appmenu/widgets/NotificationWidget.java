package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.services.notification.Notification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class NotificationWidget extends Composite implements ModelWidget<Notification> {

	private static final NotificationWidgetMessages messages = GWT.create(NotificationWidgetMessages.class);

	private static NotificationWidgetUiBinder uiBinder = GWT.create(NotificationWidgetUiBinder.class);

	interface NotificationWidgetUiBinder extends UiBinder<Widget, NotificationWidget> {}

	@UiField
	protected FocusPanel menuMouseOverArea;

	private Notification notification;

	public NotificationWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public NotificationWidget(final Notification notification) {
		this.notification = notification;

		initWidget(uiBinder.createAndBindUi(this));
		setVisible(false);

		update();

		setVisible(true);
	}

	@Override
	public boolean update() {
		return false;
	}

	public Notification getNotification() {
		return notification;
	}

	@Override
	public Notification getModelObject() {
		return getNotification();
	}

}
