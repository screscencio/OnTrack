package br.com.oncast.ontrack.client.ui.keyeventhandler.modifier;

import br.com.oncast.ontrack.client.utils.jquery.Event;

import com.google.gwt.dom.client.NativeEvent;

public enum AltModifier implements ShortcutModifier {
	PRESSED,
	UNPRESSED,
	BOTH;

	@Override
	public boolean matches(final Event e) {
		return doMatches(e.altKey());
	}

	public boolean matches(final NativeEvent e) {
		return doMatches(e.getAltKey());
	}

	private boolean doMatches(final boolean altKey) {
		if (this == BOTH) return true;
		return altKey == (this == PRESSED);
	}
}