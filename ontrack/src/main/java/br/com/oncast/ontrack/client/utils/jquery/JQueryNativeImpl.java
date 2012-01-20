package br.com.oncast.ontrack.client.utils.jquery;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

final class JQueryNativeImpl extends JavaScriptObject implements JQueryNative {
	protected JQueryNativeImpl() {}

	public native static JQueryNative jquery(final Element element) /*-{
		return $wnd.$(element, $doc);
	}-*/;

	@Override
	public native void bind(String eventType, EventHandler handler) /*-{
		this
				.bind(
						eventType,
						function(e) {
							handler.@br.com.oncast.ontrack.client.utils.jquery.EventHandler::handle(Lbr/com/oncast/ontrack/client/utils/jquery/Event;)(e);
						});
	}-*/;

	@Override
	public native void unbind(String string, EventHandler handler) /*-{
		this.unbind(eventType, handler);
	}-*/;
}
