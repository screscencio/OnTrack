package br.com.oncast.ontrack.client.ui.keyeventhandler.modifier;

import br.com.oncast.ontrack.client.utils.jquery.Event;

public enum ShiftModifier implements ShortcutModifier {
	PRESSED,
	UNPRESSED,
	BOTH;

	@Override
	public boolean matches(final Event e) {
		if (this == BOTH) return true;
		return e.shiftKey() == (this == PRESSED);
	}
}