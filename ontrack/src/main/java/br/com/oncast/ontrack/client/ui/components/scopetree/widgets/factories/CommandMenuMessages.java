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

	@Description("Done state tooltip")
	@DefaultMessage("Finished in {0}")
	String finishedIn(String date);

	@Description("Not started but some accomplished state tooltip")
	@DefaultMessage("Not in progress but {0}% done")
	String accomplished(String accomplished);

	@Description("Has open impediments state tooltip")
	@DefaultMessage("This item has open impediments, click to see them")
	String hasOpenImpediments();

	@Description("Show impediments menu item")
	@DefaultMessage("Show impediments")
	String impediments();

	@Description("posfix for due date, remaining days tooltip")
	@DefaultMessage("left")
	String left();

	@Description("posfix for expired due date, delayed days tooltip")
	@DefaultMessage("late")
	String late();

}
