package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

public class SimpleCommandMenuItem implements CommandMenuItem {

	private final Command command;
	private final String text;
	private MenuItem menuItem;
	private final String value;
	private boolean growAnimation = true;
	private boolean isRtl = false;

	public SimpleCommandMenuItem(final String text, final Command command) {
		this.text = text;
		this.value = text;
		this.command = command;
	}

	/**
	 * Creates a simple item.
	 * @param text to be shown on the items list
	 * @param value to be used instead of the shown text
	 * @param command to be executed, if null this item will NOT close the menu when activated.
	 */
	public SimpleCommandMenuItem(final String text, final String value, final Command command) {
		this.text = text;
		this.value = value;
		this.command = command;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public MenuItem getMenuItem() {
		if (menuItem != null) return menuItem;
		menuItem = new MenuItem(text, true, command);
		menuItem.setStyleName("gwt-MenuItem-noGrow", !growAnimation);
		menuItem.setStyleName("gwt-MenuItem-rtl", isRtl);
		return menuItem;
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

	public SimpleCommandMenuItem setGrowAnimation(final boolean animated) {
		growAnimation = animated;
		return this;
	}

	public SimpleCommandMenuItem setRtl(final boolean isRtl) {
		this.isRtl = isRtl;
		return this;
	}

	public boolean isRtl() {
		return isRtl;
	}
}
