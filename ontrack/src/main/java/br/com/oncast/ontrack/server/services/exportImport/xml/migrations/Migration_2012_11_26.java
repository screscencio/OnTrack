package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

import java.util.List;

import org.dom4j.Element;

/**
 * Changes:
 * <ul>
 * <li>Changes unique subAction for list of subActions in ScopeDeclareProgressAction
 * </ul>
 * 
 */
public class Migration_2012_11_26 extends Migration {

	private static final String SCOPE_DECALRE_ACTION = "br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction";
	private static final String ROLLBACK_SUB_ACTION = "rollbackSubAction";
	private static final String SUB_ACTION_LIST = "subActionList";
	private static final String MODEL_ACTION = "modelAction";

	@Override
	protected void execute() throws Exception {
		transformIntoList();
	}

	private void transformIntoList() {
		final List<Element> actions = getElementsWithClassAttribute(SCOPE_DECALRE_ACTION);
		for (final Element action : actions) {
			final Element subAction = action.element(ROLLBACK_SUB_ACTION);

			final Element subActionList = addListElementTo(action, SUB_ACTION_LIST);

			if (subAction == null) continue;

			action.remove(subAction);
			subAction.setName(MODEL_ACTION);
			subActionList.add(subAction);
		}
	}

}
