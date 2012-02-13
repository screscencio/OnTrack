package br.com.oncast.ontrack.client.services.globalEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

public class GlobalNativeEventService {

	private static GlobalNativeEventService instance;
	private final Set<NativeEventListener> keyUpListeners = new HashSet<NativeEventListener>();
	private final Set<NativeEventListener> keyDownListeners = new HashSet<NativeEventListener>();
	private final Set<NativeEventListener> clickListeners = new HashSet<NativeEventListener>();
	private final Set<NativeEventListener> mouseUpListeners = new HashSet<NativeEventListener>();

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
					case Event.ONKEYDOWN: {
						final NativeEvent nativeEvent = event.getNativeEvent();
						for (final NativeEventListener listener : new ArrayList<NativeEventListener>(keyDownListeners))
							listener.onNativeEvent(nativeEvent);
					}
						break;
					case Event.ONCLICK: {
						final NativeEvent nativeEvent = event.getNativeEvent();
						for (final NativeEventListener listener : new ArrayList<NativeEventListener>(clickListeners))
							listener.onNativeEvent(nativeEvent);
					}
						break;
					case Event.ONMOUSEUP: {
						final NativeEvent nativeEvent = event.getNativeEvent();
						for (final NativeEventListener listener : new ArrayList<NativeEventListener>(mouseUpListeners))
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

	public void addKeyDownListener(final NativeEventListener listener) {
		keyDownListeners.add(listener);
	}

	public void removeKeyDownListener(final NativeEventListener listener) {
		keyDownListeners.remove(listener);
	}

	public void addMouseUpListener(final NativeEventListener listener) {
		mouseUpListeners.add(listener);
	}

	public void removeMouseUpListener(final NativeEventListener listener) {
		mouseUpListeners.remove(listener);
	}

}