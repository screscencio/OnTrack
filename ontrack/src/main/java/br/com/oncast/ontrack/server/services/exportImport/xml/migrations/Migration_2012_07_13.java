package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

import org.dom4j.Element;

/**
 * Changes:
 * <ul>
 * <li>Adds AnnotationRemoveAction to AnnotationCreateAction's subActionsList when it's used to roll back a AnnotationRemoveAction that has child annotations
 * removed together.
 * <li>Renames AnnotationCreateAction's parameter named annotattedObjectId to subjectId
 * </ul>
 * 
 */
public class Migration_2012_07_13 extends Migration {

	private static final String ANNOTATION_CREATE_ACTION = "br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction";

	@Override
	protected void execute() throws Exception {
		for (final Element action : getElementsWithClassAttribute(ANNOTATION_CREATE_ACTION)) {
			addEmptySubActionsList(action);
			renameParameter(action);
		}
	}

	private void addEmptySubActionsList(final Element action) {
		if (subActionListNotPresent(action)) addListElementTo(action, "subActionList");
	}

	private boolean subActionListNotPresent(final Element action) {
		return action.element("subActionList") == null;
	}

	private void renameParameter(final Element action) {
		action.element("annotatedObjectId").setName("subjectId");
	}
}
