package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.searchbar;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface SearchScopeMenuMessages extends BaseMessages {

	@Description("message shown on results of the search scope menu")
	@DefaultMessage("Part of")
	String partOf();

	@Description("message with number of matching searched scopes")
	@DefaultMessage("Showing {0} matching results")
	String showingMatchingResults(int size);

}
