package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class NotificationMenuItemHeaderWidget extends Composite implements HasText {

	private static NotificationMenuItemHeaderWidgetUiBinder uiBinder = GWT.create(NotificationMenuItemHeaderWidgetUiBinder.class);

	interface NotificationMenuItemHeaderWidgetUiBinder extends UiBinder<Widget, NotificationMenuItemHeaderWidget> {}

	public NotificationMenuItemHeaderWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Label label;

	@UiField
	Label countIndicator;

	@Override
	public void setText(final String text) {
		this.label.setText(text);
	}

	@Override
	public String getText() {
		return label.getText();
	}

	public void setNotificationCountIndicator(final String countIndicatorText) {
		countIndicator.setText(countIndicatorText);
	}

}
