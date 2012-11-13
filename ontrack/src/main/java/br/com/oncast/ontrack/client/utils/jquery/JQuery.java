package br.com.oncast.ontrack.client.utils.jquery;

import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationCallback;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
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

	public JQuery show() {
		nat.show();
		return this;
	}

	public JQuery hide() {
		nat.hide();
		return this;
	}

	public JQuery clearQueue() {
		nat.clearQueue();
		return this;
	}

	public JQuery slideUp(final int duration) {
		return this.slideUp(duration, null);
	}

	public JQuery slideUp(final int duration, final AnimationCallback callback) {
		nat.slideUp(duration, createCallback(callback));
		return this;
	}

	public JQuery slideDown(final int duration) {
		return this.slideDown(duration, null);
	}

	public JQuery slideDown(final int duration, final AnimationCallback callback) {
		nat.slideDown(duration, createCallback(callback));
		return this;
	}

	public JQuery fadeIn(final int duration) {
		return this.fadeIn(duration, null);
	}

	public JQuery fadeIn(final int duration, final AnimationCallback callback) {
		nat.fadeIn(duration, createCallback(callback));
		return this;
	}

	public JQuery fadeOut(final int duration) {
		return this.fadeOut(duration, null);
	}

	public JQuery fadeOut(final int duration, final AnimationCallback callback) {
		nat.fadeOut(duration, createCallback(callback));
		return this;
	}

	public JQuery fadeTo(final int duration, final double opacity) {
		return this.fadeTo(duration, opacity, null);
	}

	public JQuery fadeTo(final int duration, final double opacity, final AnimationCallback callback) {
		nat.fadeTo(duration, opacity, createCallback(callback));
		return this;
	}

	public static JQuery jquery(final Element element) {
		return new JQuery(element);
	}

	private JQueryCallback createCallback(final AnimationCallback callback) {
		final JQueryCallback jQueryCallback = new JQueryCallback() {
			@Override
			public void onComplete() {
				if (callback != null) callback.onComplete();
			}
		};
		return jQueryCallback;
	}

	public JQuery stop(final boolean clearQueue) {
		nat.stop(clearQueue);
		return this;
	}

	public JQuery customFallInAbsolutePositioning(final int duration, final AnimationCallback callback) {
		new Timer() {

			@Override
			public void run() {
				nat.customFallInAbsolutePositioning(duration, createCallback(callback));
			}
		}.schedule(10);
		return this;
	}

}
