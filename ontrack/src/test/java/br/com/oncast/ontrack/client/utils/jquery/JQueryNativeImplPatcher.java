package br.com.oncast.ontrack.client.utils.jquery;

import com.google.gwt.dom.client.Element;
import com.octo.gwt.test.patchers.PatchClass;
import com.octo.gwt.test.patchers.PatchMethod;

@PatchClass(JQueryNativeImpl.class)
public class JQueryNativeImplPatcher {
	@PatchMethod
	static JQueryNative jquery(final Element widget) {
		return new JQueryNative() {
			@Override
			public void bind(final String eventType, final EventHandler handler) {}

			@Override
			public void unbind(final String string, final EventHandler handler) {}
		};
	}
}
