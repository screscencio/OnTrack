package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.user.client.Command;

public class CommandMenuItem implements Comparable<CommandMenuItem> {

	private final Command command;
	private final String text;

	public CommandMenuItem(final String text, final Command command) {
		this.text = text;
		this.command = command;
	}

	public Command getCommand() {
		return command;
	}

	public String getText() {
		return text;
	}

	@Override
	public int compareTo(final CommandMenuItem obj) {
		try {
			final Integer thisText = new Integer(text);
			final Integer objText = new Integer(obj.getText());
			return thisText.compareTo(objText);
		}
		catch (final NumberFormatException e) {
			return this.text.toLowerCase().compareTo(obj.text.toLowerCase());
		}
	}
}
