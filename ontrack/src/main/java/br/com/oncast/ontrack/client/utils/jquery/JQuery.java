package br.com.oncast.ontrack.client.utils.jquery;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a jQuery java-script-api wrapper. It attempts to mimic as much as possible the java script jQuery interface through GWT Java code, making it
 * accessible to GWT code and make the appropriate conversions whenever necessary.<br />
 * <br />
 * <b>Developer notice:</b> Eventually you will face the absence of some jQuery functionality. In such cases please extend this jQuery Wrapper, but keep in mind
 * it should mimic the jQuery java-script interface as much as possible.
 */
public final class JQuery {
	private final JQueryNative nat;

	private JQuery(final Element element) {
		nat = JQueryNativeImpl.jquery(element);
	}

	public static JQuery jquery(final Widget widget) {
		return new JQuery(widget.getElement());
	}

	public JQuery bindKeydown(final EventHandler handler) {
		nat.bind("keydown", handler);
		return this;
	}

	public void unbindKeyDown(final EventHandler handler) {
		nat.unbind("keydown", handler);
	}

	public static JQuery jquery(final Element element) {
		return new JQuery(element);
	}
}
