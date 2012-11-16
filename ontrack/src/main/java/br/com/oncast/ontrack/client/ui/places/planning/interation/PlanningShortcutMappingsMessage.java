package br.com.oncast.ontrack.client.ui.places.planning.interation;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface PlanningShortcutMappingsMessage extends BaseMessages {

	@DefaultMessage("search")
	@Description("Description for search shortcut")
	String searchScope();

	@DefaultMessage("Show / hide releases panel")
	@Description("Description for toggle release panel shortcut")
	String toggleReleasePanel();

}
