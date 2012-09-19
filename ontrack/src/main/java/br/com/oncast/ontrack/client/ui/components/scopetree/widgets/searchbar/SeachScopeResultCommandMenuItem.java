package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.searchbar;

import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

public class SeachScopeResultCommandMenuItem implements CommandMenuItem {

	private static final SearchScopeMenuMessages messages = GWT.create(SearchScopeMenuMessages.class);

	private final Scope scope;
	private final Command command;
	private MenuItem menuItem;

	public SeachScopeResultCommandMenuItem(final Scope scope, final Command command) {
		this.scope = scope;
		this.command = command;
	}

	@Override
	public String getText() {
		return scope.getDescription();
	}

	@Override
	public String getValue() {
		return getText();
	}

	@Override
	public MenuItem getMenuItem() {
		if (menuItem != null) return menuItem;
		final SafeHtml safeHtml = new SafeHtmlBuilder().appendHtmlConstant(
				"<div class='searchScopeResult-container'>" +
						"<span class='searchScopeResult-description'>" + scope.getDescription() + "</span>" +
						"<span class='searchScopeResult-partOf'>" + messages.partOf() + ": </span>" +
						"<span class='searchScopeResult-parentDescription'>" + scope.getParent().getDescription() + "</span>" +
						"</div>"
				)
				.toSafeHtml();
		return menuItem = new MenuItem(safeHtml, command);
	}

	@Override
	public int compareTo(final CommandMenuItem obj) {
		return getText().toLowerCase().compareTo(obj.getText().toLowerCase());
	}

	@Override
	public boolean executeCommand() {
		if (command == null) return false;
		command.execute();
		return true;
	}

}
