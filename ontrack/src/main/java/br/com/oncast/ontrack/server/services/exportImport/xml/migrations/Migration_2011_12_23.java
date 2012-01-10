package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

public class Migration_2011_12_23 extends Migration {
	@Override
	protected void execute() throws Exception {
		renamePackage("br.com.oncast.ontrack.shared.model.actions.", "br.com.oncast.ontrack.shared.model.action.");
		convertDeclaredEffortsToFloat();
	}

	private void convertDeclaredEffortsToFloat() {
		final List<Element> actions = getElementsWithClassAttribute("br.com.oncast.ontrack.shared.model.action.ScopeDeclareEffortAction");
		for (final Element a : actions) {
			final Attribute att = a.attribute("newDeclaredEffort");
			if (att.getValue().contains(".")) continue;
			att.setValue(att.getValue() + ".0");
		}
	}
}
