package br.com.oncast.ontrack.client.ui.keyeventhandler.modifier;

import br.com.oncast.ontrack.client.utils.jquery.Event;

import com.google.gwt.dom.client.NativeEvent;

public enum ShiftModifier implements ShortcutModifier {
	PRESSED,
	UNPRESSED,
	BOTH;

	@Override
	public boolean matches(final Event e) {
		return doMatches(e.shiftKey());
	}

	public boolean matches(final NativeEvent e) {
		return doMatches(e.getShiftKey());
	}

	private boolean doMatches(final boolean shiftKey) {
		if (this == BOTH) return true;
		return shiftKey == (this == PRESSED);
	}
}