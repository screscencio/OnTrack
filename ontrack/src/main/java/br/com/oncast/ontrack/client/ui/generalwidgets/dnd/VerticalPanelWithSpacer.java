package br.com.oncast.ontrack.client.ui.generalwidgets.dnd;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class VerticalPanelWithSpacer extends VerticalPanel {

	public VerticalPanelWithSpacer() {
		super();

		final Widget spacer = createEmptyWidget();
		super.add(spacer);
		setCellHeight(spacer, "100%");
	}

	@Override
	public int getWidgetCount() {
		return super.getWidgetCount() - 1;
	}

	@Override
	public void add(final Widget w) {
		this.insert(w, getLastIndex());
	}

	@Override
	public void insert(final Widget w, final int beforeIndex) {
		super.insert(w, beforeIndex);

		setCellHeight(w, "1");
	}

	private int getLastIndex() {
		return getWidgetCount() - 1;
	}

	private Widget createEmptyWidget() {
		final SimplePanel panel = new SimplePanel();
		panel.setWidth("207px");
		panel.setHeight("0px");
		return panel;
	}
}