package br.com.oncast.ontrack.client.services.globalEvent;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

public class GlobalNativeEventService {

	private static GlobalNativeEventService instance;
	private final List<NativeEventListener> keyUpListeners = new ArrayList<NativeEventListener>();
	private final List<NativeEventListener> clickListeners = new ArrayList<NativeEventListener>();

	public static GlobalNativeEventService getInstance() {
		if (instance != null) return instance;
		return instance = new GlobalNativeEventService();
	}

	private GlobalNativeEventService() {
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(final NativePreviewEvent event) {
				final int eventType = event.getTypeInt();

				switch (eventType) {
					case Event.ONKEYUP: {
						final NativeEvent nativeEvent = event.getNativeEvent();
						for (final NativeEventListener listener : new ArrayList<NativeEventListener>(keyUpListeners))
							listener.onNativeEvent(nativeEvent);
					}
						break;
					case Event.ONCLICK: {
						final NativeEvent nativeEvent = event.getNativeEvent();
						for (final NativeEventListener listener : new ArrayList<NativeEventListener>(clickListeners))
							listener.onNativeEvent(nativeEvent);
					}
						break;
				}
			}
		});
	}

	public void addClickListener(final NativeEventListener listener) {
		clickListeners.add(listener);
	}

	public void removeClickListener(final NativeEventListener listener) {
		clickListeners.remove(listener);
	}

	public void addKeyUpListener(final NativeEventListener listener) {
		keyUpListeners.add(listener);
	}

	public void removeKeyUpListener(final NativeEventListener listener) {
		keyUpListeners.remove(listener);
	}

}