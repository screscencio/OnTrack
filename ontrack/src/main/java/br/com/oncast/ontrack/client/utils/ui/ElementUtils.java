package br.com.oncast.ontrack.client.utils.ui;

import br.com.oncast.ontrack.shared.model.color.Color;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class ElementUtils {

	public static Style getStyle(final UIObject widget) {
		return widget.getElement().getStyle();
	}

	public static void setVisible(final Element element, final boolean visible) {
		if (visible) element.getStyle().clearDisplay();
		else element.getStyle().setDisplay(Display.NONE);
	}

	public static void setBackgroundColor(final Widget widget, final Color color) {
		widget.getElement().getStyle().setBackgroundColor(color.toCssRepresentation());
	}

	public static native void click(Element elem) /*-{
		elem.click();
	}-*/;

}
