package br.com.oncast.ontrack.client.ui.keyeventhandler.modifier;

import br.com.oncast.ontrack.client.utils.RuntimeEnvironment;
import br.com.oncast.ontrack.client.utils.jquery.Event;

import com.google.gwt.dom.client.NativeEvent;

public enum ControlModifier implements ShortcutModifier {
	PRESSED,
	UNPRESSED,
	BOTH;

	@Override
	public boolean matches(final Event e) {
		return doMaches(e.metaKey(), e.ctrlKey());
	}

	public boolean matches(final NativeEvent e) {
		return doMaches(e.getMetaKey(), e.getCtrlKey());
	}

	private boolean doMaches(final boolean metaKey, final boolean ctrlKey) {
		if (this == BOTH) return true;
		return (RuntimeEnvironment.isMac() ? metaKey && !ctrlKey : ctrlKey) == (this == PRESSED);
	}
}