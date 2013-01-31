package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.user.client.ui.MenuItem;

public interface CommandMenuItem extends Comparable<CommandMenuItem> {

	String getText();

	String getValue();

	MenuItem getMenuItem();

	@Override
	int compareTo(final CommandMenuItem obj);

	boolean executeCommand();

}