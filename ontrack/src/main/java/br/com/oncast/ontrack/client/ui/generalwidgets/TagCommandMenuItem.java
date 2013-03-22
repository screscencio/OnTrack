package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetMenuItem;

public class TagCommandMenuItem implements CommandMenuItem {

	private final WidgetMenuItem widgetMenuItem;
	private final Command command;
	private final String text;

	public TagCommandMenuItem(final Widget widget, final String text, final Command cmd) {
		this.text = text;
		this.command = cmd;
		widgetMenuItem = new WidgetMenuItem(widget, cmd);
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public String getValue() {
		return text;
	}

	@Override
	public MenuItem getMenuItem() {
		return widgetMenuItem;
	}

	@Override
	public int compareTo(final CommandMenuItem obj) {
		return this.text.toLowerCase().compareTo(obj.getText().toLowerCase());
	}

	@Override
	public boolean executeCommand() {
		if (command == null) return false;
		command.execute();
		return true;
	}

	public TagCommandMenuItem setGrowAnimation(final boolean enabled) {
		widgetMenuItem.setStyleName("gwt-MenuItem-noGrow", !enabled);
		return this;
	}
}
