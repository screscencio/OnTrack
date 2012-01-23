package br.com.oncast.ontrack.client.ui.keyeventhandler.modifier;

import br.com.oncast.ontrack.client.utils.jquery.Event;

public enum AltModifier implements ShortcutModifier {
	PRESSED,
	UNPRESSED,
	BOTH;

	@Override
	public boolean matches(final Event e) {
		if (this == BOTH) return true;
		return e.altKey() == (this == PRESSED);
	}
}