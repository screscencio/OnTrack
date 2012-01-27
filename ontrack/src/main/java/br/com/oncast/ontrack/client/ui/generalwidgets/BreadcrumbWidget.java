package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.Widget;

public class BreadcrumbWidget extends MenuBar {

	public BreadcrumbWidget() {
		addStyleDependentName("breadcrumb");
		getElement().getStyle().setMarginTop(3, Unit.PX);
	}

	@Override
	public MenuItemSeparator addSeparator() {
		final MenuItemSeparator sep = super.addSeparator();
		DOM.setInnerHTML((Element) sep.getElement().getFirstChildElement(), ">");
		return sep;
	}

	public MenuItem addPopupItem(final String text, final Widget widgetToPopup) {
		final MenuItem item = super.addItem(text, new Command() {
			@Override
			public void execute() {}
		});
		final PopupConfig config = PopupConfig.configPopup().popup(widgetToPopup).alignBelow(item).alignRight(item);
		item.setCommand(new Command() {
			@Override
			public void execute() {
				config.pop();
			}
		});
		return item;
	}
}
