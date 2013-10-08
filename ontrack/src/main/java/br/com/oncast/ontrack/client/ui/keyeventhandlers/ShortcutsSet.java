package br.com.oncast.ontrack.client.ui.keyeventhandlers;

import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.utils.jquery.Event;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.dom.client.NativeEvent;

public class ShortcutsSet implements Iterable<Shortcut> {

	private final HashSet<Shortcut> shortcuts;

	public ShortcutsSet(final Shortcut... shortcuts) {
		assert shortcuts.length > 0;
		this.shortcuts = new HashSet<Shortcut>(Arrays.asList(shortcuts));
	}

	public boolean accepts(final NativeEvent event) {
		for (final Shortcut s : shortcuts) {
			if (s.accepts(event)) return true;
		}
		return false;
	}

	public boolean accepts(final Event event) {
		for (final Shortcut s : shortcuts) {
			if (s.accepts(event)) return true;
		}
		return false;
	}

	public Set<Shortcut> asSet() {
		return new HashSet<Shortcut>(shortcuts);
	}

	@Override
	public Iterator<Shortcut> iterator() {
		return shortcuts.iterator();
	}

}