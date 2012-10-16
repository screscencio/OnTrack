package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.searchbar;

import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

public class SearchScopeResultCommandMenuItem implements CommandMenuItem {

	public interface SearchScopeResultCommandMenuItemTemplates extends SafeHtmlTemplates {
		@Template("<div class='searchScopeResult-container'>" +
				"<span class='searchScopeResult-description'>{0}</span>" +
				"<span class='searchScopeResult-partOf'>{1}: </span>" +
				"<span class='searchScopeResult-parentDescription'>{2}</span>" +
				"</div>")
		SafeHtml searchResultItem(String scopeDescription, String partOfString, String parentDescription);
	}

	private static final SearchScopeResultCommandMenuItemTemplates TEMPLATES = GWT.create(SearchScopeResultCommandMenuItemTemplates.class);

	private static final SearchScopeMenuMessages messages = GWT.create(SearchScopeMenuMessages.class);

	private final Scope scope;
	private final Command command;
	private MenuItem menuItem;

	public SearchScopeResultCommandMenuItem(final Scope scope, final Command command) {
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

		final SafeHtml safeHtml = TEMPLATES.searchResultItem(scope.getDescription(), messages.partOf(), scope.getParent().getDescription());
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
