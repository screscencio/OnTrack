package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class WidgetVisibilityAssurer {

	// IMPORTANT This margin must be at least 40px, because some HTML elements may have a horizontal scroll which impacts the visibility assurance.
	private static final int MARGIN = 40;

	private final Widget widget;

	public WidgetVisibilityAssurer(final Widget widget) {
		this.widget = widget;
	}

	public void assureVisibilityAround(final Widget relativeWidget) {
		final Element element = widget.getElement();
		final Element relativeElement = relativeWidget.getElement();

		DOM.setStyleAttribute(element, "marginTop", "0px");

		final int deltaTop = relativeElement.getAbsoluteTop() - element.getClientHeight() - MARGIN;
		final int deltaBottom = Window.getClientHeight()
				- (relativeElement.getAbsoluteTop() + relativeElement.getClientHeight() + element.getClientHeight() + MARGIN);

		final boolean shouldDisplayAbove = deltaBottom < 0 && deltaTop > 0;
		final int margin = shouldDisplayAbove ?
				(relativeElement.getAbsoluteTop() - element.getClientHeight() - element.getAbsoluteTop()) :
				(relativeElement.getAbsoluteTop() + relativeElement.getClientHeight() - element.getAbsoluteTop());

		DOM.setStyleAttribute(element, "marginTop", margin + "px");
	}

	public void assureVisibility() {
		final Element element = widget.getElement();

		DOM.setStyleAttribute(element, "marginTop", "0px");

		final int deltaBottom = (element.getAbsoluteTop() + element.getClientHeight() + MARGIN) - Window.getClientHeight();
		final int deltaTop = element.getAbsoluteTop() - element.getClientHeight() - MARGIN;

		if (!(deltaBottom > 0 && deltaTop > 0)) return;

		DOM.setStyleAttribute(element, "marginTop", "-" + element.getClientHeight() + "px");
	}
}
