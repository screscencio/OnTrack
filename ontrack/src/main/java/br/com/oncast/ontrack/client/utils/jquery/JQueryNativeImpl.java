package br.com.oncast.ontrack.client.utils.jquery;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

final class JQueryNativeImpl extends JavaScriptObject implements JQueryNative {
	protected JQueryNativeImpl() {}

	public native static JQueryNative jquery(final Element element) /*-{
		return $wnd.$(element, $doc);
	}-*/;

	@Override
	public native void slideUp(int duration) /*-{
		this.slideUp(duration);
	}-*/;

	@Override
	public native void slideDown(int duration) /*-{
		this.slideDown(duration);
	}-*/;

	@Override
	public native void hide() /*-{
		this.hide();
	}-*/;

	@Override
	public native void bind(String eventType, EventHandler handler) /*-{
		this
				.bind(
						eventType
								+ "."
								+ handler.@br.com.oncast.ontrack.client.utils.jquery.EventHandler::registrationId,
						function(e) {
							handler.@br.com.oncast.ontrack.client.utils.jquery.EventHandler::handle(Lbr/com/oncast/ontrack/client/utils/jquery/Event;)(e);
						});
	}-*/;

	@Override
	public native void unbind(String eventType, EventHandler handler) /*-{
		this
				.unbind(eventType
						+ "."
						+ handler.@br.com.oncast.ontrack.client.utils.jquery.EventHandler::registrationId);
	}-*/;
}
