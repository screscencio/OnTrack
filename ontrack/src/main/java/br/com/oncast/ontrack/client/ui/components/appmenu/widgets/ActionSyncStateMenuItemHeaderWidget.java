package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.utils.ui.ElementUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class ActionSyncStateMenuItemHeaderWidget extends Composite implements HasText {

	private static ActionSyncStateMenuItemHeaderWidgetUiBinder uiBinder = GWT.create(ActionSyncStateMenuItemHeaderWidgetUiBinder.class);

	interface ActionSyncStateMenuItemHeaderWidgetUiBinder extends UiBinder<Widget, ActionSyncStateMenuItemHeaderWidget> {}

	interface ActionSyncStateMenuItemHeaderWidgetStyle extends CssResource {
		String connected();
	}

	public ActionSyncStateMenuItemHeaderWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	ActionSyncStateMenuItemHeaderWidgetStyle style;

	@UiField
	HTMLPanel container;

	@UiField
	Element icon;

	@UiField
	SpanElement counter;

	public void setIcon(final String styleName) {
		icon.setClassName(styleName);
	}

	@Override
	public void setText(final String text) {
		counter.setInnerText(text);
	}

	@Override
	public String getText() {
		return counter.getInnerText();
	}

	public void setCounterLabelVisible(final boolean visible) {
		ElementUtils.setVisible(counter, visible);
	}

	public void setConnected(final boolean connected) {
		container.setStyleName(style.connected(), connected);
	}

}
