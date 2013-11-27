package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

import org.dom4j.Element;

/**
 * Changes:
 * <ul>
 * <li>Removes subActionList from ScopeRemoveAction
 * </ul>
 * 
 */
public class Migration_2012_07_23 extends Migration {

	private static final String SCOPE_REMOVE_ACTION = "br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction";

	@Override
	protected void execute() throws Exception {
		for (final Element action : getElementsWithClassAttribute(SCOPE_REMOVE_ACTION)) {
			final Element subActionList = action.element("subActionList");
			action.remove(subActionList);
		}
	}
}
