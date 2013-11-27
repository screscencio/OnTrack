package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.dom4j.Element;

/**
 * Changes:
 * <ul>
 * <li>Adds columnId attribute to KanbanColumnCreateAction
 * </ul>
 * 
 */
public class Migration_2013_05_26 extends Migration {

	private static final String KANBAN_COLUMN_CREATE = "br.com.oncast.ontrack.shared.model.action.KanbanColumnCreateAction";

	private static final String COLUMN_ID = "columnId";

	private static final String ID = "id";

	@Override
	protected void execute() throws Exception {
		for (final Element action : getElementsWithClassAttribute(KANBAN_COLUMN_CREATE)) {
			if (action.element(COLUMN_ID) == null) action.addElement(COLUMN_ID).addAttribute(ID, new UUID().toString());
		}
	}

}
