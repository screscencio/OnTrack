package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class WidgetVisibilityAssurer {

	private final Widget widget;

	public WidgetVisibilityAssurer(final Widget widget) {
		this.widget = widget;
	}

	public void assureVisibility() {
		final Element element = widget.getElement();
		final int MARGIN = 35;
		final int deltaBottom = (element.getAbsoluteTop() + element.getClientHeight() + MARGIN) - Window.getClientHeight();
		final int deltaTop = element.getAbsoluteTop() - element.getClientHeight() - MARGIN;

		if (!(deltaBottom > 0 && deltaTop > 0)) return;

		DOM.setStyleAttribute(element, "marginTop", "-" + element.getClientHeight() + "px");
	}
}
