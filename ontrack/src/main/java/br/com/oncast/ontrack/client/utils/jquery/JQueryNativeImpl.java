package br.com.oncast.ontrack.client.utils.jquery;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

final class JQueryNativeImpl extends JavaScriptObject implements JQueryNative {
	protected JQueryNativeImpl() {}

	public native static JQueryNative jquery(final Element element) /*-{
		return $wnd.$(element, $doc);
	}-*/;

	@Override
	public native void slideUp(final int duration, final JQueryCallback callback) /*-{
		var jscallback = function() {
			callback.@br.com.oncast.ontrack.client.utils.jquery.JQueryCallback::onComplete()();
		}
		this.slideUp(duration, jscallback);
	}-*/;

	@Override
	public native void slideDown(final int duration, final JQueryCallback callback) /*-{
		var jscallback = function() {
			callback.@br.com.oncast.ontrack.client.utils.jquery.JQueryCallback::onComplete()();
		}
		this.slideDown(duration, jscallback);
	}-*/;

	@Override
	public native void fadeIn(final int duration, final JQueryCallback callback) /*-{
		var jscallback = function() {
			callback.@br.com.oncast.ontrack.client.utils.jquery.JQueryCallback::onComplete()();
		}
		this.fadeIn(duration, jscallback);
	}-*/;

	@Override
	public native void fadeOut(final int duration, final JQueryCallback callback) /*-{
		var jscallback = function() {
			callback.@br.com.oncast.ontrack.client.utils.jquery.JQueryCallback::onComplete()();
		}
		this.fadeOut(duration, jscallback);
	}-*/;

	@Override
	public native void fadeTo(final int duration, final double opacity, final JQueryCallback callback) /*-{
		var jscallback = function() {
			callback.@br.com.oncast.ontrack.client.utils.jquery.JQueryCallback::onComplete()();
		}
		this.fadeTo(duration, opacity, jscallback);
	}-*/;

	@Override
	public native void show() /*-{
		this.show();
	}-*/;

	@Override
	public native void hide() /*-{
		this.hide();
	}-*/;

	@Override
	public native void clearQueue() /*-{
		this.clearQueue();
	}-*/;

	@Override
	public native void stop(boolean clearQueue) /*-{
		this.stop(clearQueue);
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

	@Override
	public native void customDropDownAbsolutePositioning(final int d, final JQueryCallback callback) /*-{
		var jscallback = function() {
			callback.@br.com.oncast.ontrack.client.utils.jquery.JQueryCallback::onComplete()();
		}

		var cheight = this.outerHeight();
		var ctop = this.css("top");
		var cbottom = this.css("bottom");
		var wind = $wnd.$($wnd);

		this.css({
			top : wind.scrollTop() - (cheight / 3),
			bottom : wind.height(),
			opacity : 1,
			display : 'block'
		});

		this.animate({
			top : ctop,
			bottom : cbottom,
		}, {
			duration : d,
			complete : jscallback
		});

	}-*/;

	@Override
	public native void customDropUpAbsolutePositioning(final int d, final JQueryCallback callback) /*-{
		var jscallback = function() {
			callback.@br.com.oncast.ontrack.client.utils.jquery.JQueryCallback::onComplete()();
		}

		var cheight = this.outerHeight();
		var ctop = this.css("top");
		var cbottom = this.css("bottom");
		var wind = $wnd.$($wnd);

		this.css({
			top : ctop,
			bottom : cbottom,
			opacity : 1,
			display : 'block'
		});

		this.animate({
			top : wind.scrollTop() - (cheight / 3),
			bottom : wind.height(),
		}, {
			duration : d,
			complete : jscallback
		});

	}-*/;

}
