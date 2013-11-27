package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

import org.dom4j.Element;

/**
 * Changes:
 * <ul>
 * <li>Adds ScopeDeclareValueAction to ScopeRemoveActions' subActionsList.
 * </ul>
 * 
 */
public class Migration_2012_07_12 extends Migration {

	private static final String SCOPE_REMOVE_ACTION = "br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction";
	private static final String SCOPE_DECLARE_VALUE_ACTION = "br.com.oncast.ontrack.shared.model.action.ScopeDeclareValueAction";

	@Override
	protected void execute() throws Exception {
		for (final Element removeAction : getElementsWithClassAttribute(SCOPE_REMOVE_ACTION)) {
			addScopeDeclareValueActionAsSubAction(removeAction);
		}
	}

	private void addScopeDeclareValueActionAsSubAction(final Element removeAction) {
		final String referenceId = removeAction.element("referenceId").attributeValue("id");
		final Element subActionList = removeAction.element("subActionList");

		addElementWithClassAttribute(subActionList, "modelAction", SCOPE_DECLARE_VALUE_ACTION)
				.addAttribute("hasDeclaredValue", "false")
				.addAttribute("newDeclaredValue", "0.0")
				.addElement("referenceId")
				.addAttribute("id", referenceId);
	}

}
