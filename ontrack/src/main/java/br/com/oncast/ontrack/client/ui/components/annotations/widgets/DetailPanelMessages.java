package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface DetailPanelMessages extends BaseMessages {

	@Description("deprecated message")
	@DefaultMessage("[Deprecated] {0}")
	String deprecated(String message);

	@Description("deprecation details message")
	@DefaultMessage("[Deprecated by {0} since {1}]")
	String deprecationDetails(String username, String formattedDate);

	@Description("empty checklist title error message")
	@DefaultMessage("Can''t create a checklist with empty title.")
	String emptyChecklistTitleError();

	@Description("empty checklist item error message")
	@DefaultMessage("Can''t create a item with empty description.")
	String emptyChecklistItemError();

}
