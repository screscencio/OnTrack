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
	private final String imageStyle;
	private boolean growAnimation;

	public TextAndImageCommandMenuItem(final String imageStyle, final String text, final Command command) {
		this(imageStyle, text, text, command);
	}

	public TextAndImageCommandMenuItem(final String imageStyle, final String text, final String value, final Command command) {
		this.imageStyle = imageStyle;
		this.text = text;
		this.value = value;
		this.command = command;
	}

	// FIXME Xizz Remove this and update the release widget
	public TextAndImageCommandMenuItem(final ImageResource reportIcon, final String text, final Command command) {
		this.imageStyle = "icon-ban-circle";
		this.text = text;
		this.value = text;
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
				"<i class='" + imageStyle + "' style='margin-right: 10px;'></i>" +
						"<span style='line-height: 18px;'>" + text + "</span>")
				.toSafeHtml();
		menuItem = new MenuItem(safeHtml, command);
		menuItem.setStyleName("gwt-MenuItem-noGrow", !growAnimation);
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

	public TextAndImageCommandMenuItem setGrowAnimation(final boolean enabled) {
		growAnimation = enabled;
		return this;
	}
}
