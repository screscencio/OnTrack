package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class NotificationMenuItemHeaderWidget extends Composite implements HasText, HasClickHandlers {

	private static NotificationMenuItemHeaderWidgetUiBinder uiBinder = GWT.create(NotificationMenuItemHeaderWidgetUiBinder.class);

	interface NotificationMenuItemHeaderWidgetUiBinder extends UiBinder<Widget, NotificationMenuItemHeaderWidget> {}

	interface NotificationMenuItemHeaderWidgetStyle extends CssResource {
		String read();

		String unread();
	}

	@UiField
	NotificationMenuItemHeaderWidgetStyle style;

	@UiField
	Label countIndicator;

	public NotificationMenuItemHeaderWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setNotificationCountIndicator(final String countIndicatorText) {
		countIndicator.setText(countIndicatorText);
	}

	@Override
	public String getText() {
		return countIndicator.getText();
	}

	@Override
	public void setText(final String text) {}

	public void setHasUnread(final boolean hasUnread) {
		countIndicator.setStyleName(style.unread(), hasUnread);
	}

	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		return countIndicator.addClickHandler(handler);
	}

}
