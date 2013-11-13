package br.com.oncast.ontrack.client.ui.keyeventhandler;

import br.com.oncast.ontrack.client.ui.keyeventhandlers.ShortcutsSet;

public interface ShortcutMapping<T> {

	public void execute(T target);

	public String getDescription();

	public ShortcutsSet getShortcuts();

	public EventProcessor getEventPostExecutionProcessor();

	public String name();
}