package br.com.oncast.ontrack.client.utils.jquery;

import com.google.gwt.dom.client.Element;
import com.googlecode.gwt.test.patchers.PatchClass;
import com.googlecode.gwt.test.patchers.PatchMethod;

@PatchClass(JQueryNativeImpl.class)
public class JQueryNativeImplPatcher {
	@PatchMethod
	static JQueryNative jquery(final Element widget) {
		return new JQueryNative() {
			@Override
			public void bind(final String eventType, final EventHandler handler) {}

			@Override
			public void unbind(final String string, final EventHandler handler) {}

			@Override
			public void hide() {}

			@Override
			public void show() {}

			@Override
			public void slideUp(final int duration, final JQueryCallback callback) {}

			@Override
			public void slideDown(final int duration, final JQueryCallback callback) {}

			@Override
			public void fadeIn(final int duration, final JQueryCallback callback) {}

			@Override
			public void fadeOut(final int duration, final JQueryCallback callback) {}

			@Override
			public void clearQueue() {}

			@Override
			public void fadeTo(final int duration, final double opacity, final JQueryCallback createCallback) {}

			@Override
			public void stop(final boolean clearQueue) {}

			@Override
			public void customDropDownAbsolutePositioning(final int duration, final JQueryCallback createCallback) {}

			@Override
			public void customDropUpAbsolutePositioning(final int duration, final JQueryCallback createCallback) {}

			@Override
			public void slideLeftHide(final int duration, final JQueryCallback callback) {}

			@Override
			public void slideRightShow(final int duration, final JQueryCallback callback) {}
		};
	}
}
