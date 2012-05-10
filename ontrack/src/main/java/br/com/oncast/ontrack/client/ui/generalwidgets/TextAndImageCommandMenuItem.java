package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

public class TextAndImageCommandMenuItem implements CommandMenuItem {

	private final Command command;
	private final String text;
	private MenuItem menuItem;
	private final String value;
	private final ImageResource image;

	public TextAndImageCommandMenuItem(final ImageResource image, final String text, final Command command) {
		this(image, text, text, command);
	}

	public TextAndImageCommandMenuItem(final ImageResource image, final String text, final String value, final Command command) {
		this.image = image;
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
		final SafeHtml safeHtml = new SafeHtmlBuilder().appendHtmlConstant(
				"<img src='" + image.getSafeUri().asString() + "' style='width: " + image.getWidth() + "px; height: " + image.getHeight()
						+ "px;'><span style='margin-left:5px;display: inline;'>" + text + "</span>")
				.toSafeHtml();
		return menuItem = new MenuItem(safeHtml, command);
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
}
