package br.com.oncast.ontrack.client.utils.jquery;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a jQuery java-script-api wrapper. It attempts to mimic as much as possible the java script jQuery interface through GWT Java code, making it
 * accessible to GWT code and make the appropriate conversions whenever necessary.<br />
 * <br />
 * <b>Developer notice:</b> Eventually you will face the absence of some jQuery functionality. In such cases please extend this jQuery Wrapper, but keep in mind
 * it should mimic the jQuery java-script interface as much as possible.
 */
public final class JQuery extends JavaScriptObject {
	protected JQuery() {}

	public static JQuery jquery(final Widget widget) {
		return jquery(widget.getElement());
	}

	private native static JQuery jquery(final Element element) /*-{
		return $wnd.$(element, $doc);
	}-*/;

	public JQuery keydown(final EventHandler handler) {
		bind("keydown", handler);
		return this;
	}

	public JQuery keyup(final EventHandler handler) {
		bind("keyup", handler);
		return this;
	}

	private native void bind(String eventType, EventHandler handler) /*-{
		this
				.bind(
						eventType,
						function(e) {
							handler.@br.com.oncast.ontrack.client.utils.jquery.EventHandler::handle(Lbr/com/oncast/ontrack/client/utils/jquery/Event;)(e);
						});
	}-*/;
}
