package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

public class CommandMenuItem implements Comparable<CommandMenuItem> {

	private final Command command;
	private final String text;
	private MenuItem menuItem;
	private final String value;

	public CommandMenuItem(final String text, final Command command) {
		this.text = text;
		this.value = text;
		this.command = command;
	}

	public CommandMenuItem(final String text, final String value, final Command command) {
		this.text = text;
		this.value = value;
		this.command = command;
	}

	public String getText() {
		return text;
	}

	public String getValue() {
		return value;
	}

	public MenuItem getMenuItem() {
		if (menuItem != null) return menuItem;
		return menuItem = new MenuItem(text, true, command);
	}

	@Override
	public int compareTo(final CommandMenuItem obj) {
		return this.text.toLowerCase().compareTo(obj.text.toLowerCase());
	}

	public boolean executeCommand() {
		if (command == null) return false;
		command.execute();
		return true;
	}
}
