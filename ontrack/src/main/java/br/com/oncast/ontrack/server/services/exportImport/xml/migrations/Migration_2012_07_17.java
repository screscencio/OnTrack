package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * Changes:
 * <ul>
 * <li>Converts AnnotationCreateAction's message attribute into element to keep line breaks and other special characters
 * </ul>
 * 
 */
public class Migration_2012_07_17 extends Migration {

	private static final String ANNOTATION_CREATE_ACTION = "br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction";
	private static final String MESSAGE = "message";

	@Override
	protected void execute() throws Exception {
		for (final Element action : getElementsWithClassAttribute(ANNOTATION_CREATE_ACTION)) {
			final String messageValue = removeMessageAttribute(action);
			addMessageElement(action, messageValue);
		}
	}

	private void addMessageElement(final Element action, final String messageValue) {
		action.addElement(MESSAGE).addText(messageValue);
	}

	private String removeMessageAttribute(final Element action) {
		final Attribute attribute = action.attribute(MESSAGE);
		action.remove(attribute);
		return attribute.getText();
	}

}
