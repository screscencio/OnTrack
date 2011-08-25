package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.user.client.Command;

public class CommandMenuItem {

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
}
