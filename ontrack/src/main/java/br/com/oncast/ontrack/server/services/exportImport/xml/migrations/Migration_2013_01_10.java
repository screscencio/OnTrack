package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

import org.dom4j.Element;

/**
 * Changes:
 * <ul>
 * <li>Rename ScopeAddAssociatedUserAction's field tagId to metadataId
 * </ul>
 * 
 */
public class Migration_2013_01_10 extends Migration {

	private static final String METADATA_ID = "metadataId";
	private static final String TAG_ID = "tagId";
	private static final String ID = "id";
	private static final String SCOPE_ADD_ASSOCIATED_USER_ACTION = "br.com.oncast.ontrack.shared.model.action.ScopeAddAssociatedUserAction";

	@Override
	protected void execute() throws Exception {
		for (final Element action : getElementsWithClassAttribute(SCOPE_ADD_ASSOCIATED_USER_ACTION)) {
			final Element tag = action.element(TAG_ID);
			action.remove(tag);
			action.addElement(METADATA_ID).addAttribute(ID, tag.attributeValue(ID));
		}
	}

}
