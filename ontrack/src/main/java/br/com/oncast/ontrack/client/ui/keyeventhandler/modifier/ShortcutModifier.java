package br.com.oncast.ontrack.client.ui.keyeventhandler.modifier;

import br.com.oncast.ontrack.client.utils.jquery.Event;

public interface ShortcutModifier {
	public boolean matches(final Event e);
}