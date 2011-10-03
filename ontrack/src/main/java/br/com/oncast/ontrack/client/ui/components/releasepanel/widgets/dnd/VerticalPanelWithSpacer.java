package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class VerticalPanelWithSpacer extends VerticalPanel {

	private int beforeIndex;

	public VerticalPanelWithSpacer() {}

	@Override
	public void add(final Widget w) {
		super.insert(w, getWidgetCount() - 1);
	}

	@Override
	public void insert(final Widget w, int beforeIndex) {
		if (beforeIndex == getWidgetCount() && beforeIndex > 0) {
			beforeIndex--;
		}
		this.beforeIndex = beforeIndex;
		super.insert(w, beforeIndex);
	}

	public int getLastIndex() {
		return beforeIndex;
	}
}