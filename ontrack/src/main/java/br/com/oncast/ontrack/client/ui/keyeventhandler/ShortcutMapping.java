package br.com.oncast.ontrack.client.ui.keyeventhandler;


public interface ShortcutMapping<T> {

	public void execute(T target);

	public Shortcut getShortcut();

	public EventProcessor getEventPostExecutionProcessor();
}