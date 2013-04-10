package br.com.oncast.ontrack.client.utils.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.UIObject;

public class ElementUtils {

	public static Style getStyle(final UIObject widget) {
		return widget.getElement().getStyle();
	}

	public static void setVisible(final Element element, final boolean visible) {
		if (visible) element.getStyle().clearDisplay();
		else element.getStyle().setDisplay(Display.NONE);
	}

}
