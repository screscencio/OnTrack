package br.com.oncast.ontrack.client.services.globalEvent;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

public class GlobalNativeEventService {

	private final List<NativeEventListener> keyUpListeners = new ArrayList<NativeEventListener>();

	public static GlobalNativeEventService getInstance() {
		return new GlobalNativeEventService();
	}

	private GlobalNativeEventService() {
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(final NativePreviewEvent event) {
				final int eventType = event.getTypeInt();

				switch (eventType) {
				case Event.ONKEYUP:
					final NativeEvent nativeEvent = event.getNativeEvent();
					for (final NativeEventListener listener : keyUpListeners)
						listener.onNativeEvent(nativeEvent);
				}
			}
		});
	}

	public void addKeyUpListener(final NativeEventListener listener) {
		keyUpListeners.add(listener);
	}

	public void removeKeyUpListener(final NativeEventListener listener) {
		keyUpListeners.remove(listener);
	}

}