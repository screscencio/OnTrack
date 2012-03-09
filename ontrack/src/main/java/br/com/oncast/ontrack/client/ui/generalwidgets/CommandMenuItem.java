package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.user.client.ui.MenuItem;

public interface CommandMenuItem extends Comparable<CommandMenuItem> {

	public abstract String getText();

	public abstract String getValue();

	public abstract MenuItem getMenuItem();

	@Override
	public abstract int compareTo(final CommandMenuItem obj);

	public abstract boolean executeCommand();

}