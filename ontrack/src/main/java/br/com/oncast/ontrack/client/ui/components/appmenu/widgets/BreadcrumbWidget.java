package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.Widget;

import static br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment.RIGHT;

public class BreadcrumbWidget extends MenuBar {

	public BreadcrumbWidget() {
		addStyleDependentName("breadcrumb");
	}

	@Override
	public void clearItems() {
		super.clearItems();
		addStyleDependentName("breadcrumb");
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

		final PopupConfig config = PopupConfig.configPopup().popup(widgetToPopup).alignVertical(VerticalAlignment.TOP, new AlignmentReference(item, VerticalAlignment.BOTTOM, 0))
				.alignHorizontal(RIGHT, new AlignmentReference(item, RIGHT));
		item.setScheduledCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				config.pop();
			}
		});
		return item;
	}
}
