package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

public class SpacerCommandMenuItem implements CommandMenuItem {

	private MenuItem menuItem;

	@Override
	public String getText() {
		return "";
	}

	@Override
	public String getValue() {
		return "";
	}

	@Override
	public MenuItem getMenuItem() {
		if (menuItem != null) return menuItem;

		menuItem = new MenuItem("", new Command() {
			@Override
			public void execute() {}
		});
		menuItem.setEnabled(false);
		menuItem.addStyleName("gwt-MenuItem-noGrow gwt-MenuItem-Spacer");
		return menuItem;
	}

	@Override
	public boolean executeCommand() {
		return false;
	}

	@Override
	public int compareTo(final CommandMenuItem obj) {
		return 0;
	}

}
