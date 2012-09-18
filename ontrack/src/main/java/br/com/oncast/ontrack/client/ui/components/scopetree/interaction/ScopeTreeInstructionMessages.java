package br.com.oncast.ontrack.client.ui.components.scopetree.interaction;

import com.google.gwt.i18n.client.LocalizableResource.Generate;
import com.google.gwt.i18n.client.LocalizableResource.GenerateKeys;
import com.google.gwt.i18n.client.Messages;

@Generate(format = { "com.google.gwt.i18n.rebind.format.PropertiesFormat" }, locales = { "default" })
@GenerateKeys
public interface ScopeTreeInstructionMessages extends Messages {

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
