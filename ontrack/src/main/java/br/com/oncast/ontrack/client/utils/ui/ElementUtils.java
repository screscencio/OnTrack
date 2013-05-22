package br.com.oncast.ontrack.client.utils.ui;

import br.com.oncast.ontrack.shared.model.color.Color;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.UIObject;

public class ElementUtils {

	public static Style getStyle(final UIObject widget) {
		return getStyle(widget.getElement());
	}

	public static Style getStyle(final Element element) {
		return element.getStyle();
	}

	public static void setVisible(final Element element, final boolean visible) {
		if (visible) element.getStyle().clearDisplay();
		else element.getStyle().setDisplay(Display.NONE);
	}

	public static void setBackgroundColor(final UIObject widget, final Color color) {
		setBackgroundColor(getStyle(widget), color, false);
	}

	public static void setBackgroundColor(final Element element, final Color color, final boolean clearBackgroundOnTransparent) {
		setBackgroundColor(getStyle(element), color, clearBackgroundOnTransparent);
	}

	public static void setBackgroundColor(final Style style, final Color color, final boolean clearBackgroundOnTransparent) {
		style.setBackgroundColor(color.toCssRepresentation());
		if (clearBackgroundOnTransparent && (Color.TRANSPARENT.equals(color) || color == null)) style.clearBackgroundColor();
	}

	public static native void click(Element elem) /*-{
		elem.click();
	}-*/;

	public static void setClassName(final Element element, final String style, final boolean add) {
		if (add) element.addClassName(style);
		else element.removeClassName(style);
	}

}
