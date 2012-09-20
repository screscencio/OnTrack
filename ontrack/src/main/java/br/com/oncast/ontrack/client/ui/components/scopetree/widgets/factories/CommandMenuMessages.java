package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface CommandMenuMessages extends BaseMessages {

	@Description("effort command menu custom item message")
	@DefaultMessage("Use ''{0}''")
	String use(String inputText);

	@Description("progress command menu custom item message")
	@DefaultMessage("Mark as ''{0}''")
	String markAs(String inputText);

	@Description("release command menu custom item message")
	@DefaultMessage("Create ''{0}''")
	String create(String inputText);

}
