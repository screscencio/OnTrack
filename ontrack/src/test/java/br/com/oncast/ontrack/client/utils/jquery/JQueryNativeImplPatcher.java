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
			public void slideUp(final int duration) {}

			@Override
			public void slideDown(final int duration) {}

			@Override
			public void hide() {}
		};
	}
}
