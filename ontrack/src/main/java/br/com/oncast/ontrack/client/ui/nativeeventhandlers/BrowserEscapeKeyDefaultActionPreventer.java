package br.com.oncast.ontrack.client.ui.nativeeventhandlers;

import br.com.oncast.ontrack.client.services.globalEvent.NativeEventListener;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;

public class BrowserEscapeKeyDefaultActionPreventer implements NativeEventListener {
	@Override
	public void onNativeEvent(final NativeEvent nativeEvent) {
		if (nativeEvent.getKeyCode() == KeyCodes.KEY_ESCAPE) {
			nativeEvent.preventDefault();
		}
	}
}
