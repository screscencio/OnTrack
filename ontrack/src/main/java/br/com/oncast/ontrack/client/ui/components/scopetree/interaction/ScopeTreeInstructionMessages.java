package br.com.oncast.ontrack.client.ui.components.scopetree.interaction;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface ScopeTreeInstructionMessages extends BaseMessages {

	@Description("message shown in instruction panel")
	@DefaultMessage("CONTROL + ENTER to insert a child item")
	String insertChild();

	@Description("message shown in instruction panel")
	@DefaultMessage("ENTER to insert below")
	String insertBelow();

	@Description("message shown in instruction panel")
	@DefaultMessage("SHIFT + ENTER to insert above")
	String insertAbove();

	@Description("message shown in instruction panel")
	@DefaultMessage("CONTROL + ARROWS to move an Item")
	String moveItems();

	@Description("message shown in instruction panel")
	@DefaultMessage("CONTROL + SHIFT + ENTER to insert a parent item")
	String insertParent();
}
