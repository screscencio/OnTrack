package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

public class Migration_2012_02_08 extends Migration {

	@Override
	protected void execute() throws Exception {
		updateReleaseCreateActionClassToItsNewName();
		updateScopeBindReleaseActionsInternalActionsFieldToAcceptAnyModelAction();
	}

	private void updateReleaseCreateActionClassToItsNewName() {
		renameClass("br.com.oncast.ontrack.shared.model.action", "ReleaseCreateActionDefault", "ReleaseCreateAction");
	}

	private void updateScopeBindReleaseActionsInternalActionsFieldToAcceptAnyModelAction() {
		final List<Element> releaseCreateElements = getElements("//*[@class='br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction']/releaseCreateAction");
		for (final Element element : releaseCreateElements) {
			final Attribute attribute = element.attribute("class");
			if (attribute != null) continue;
			element.addAttribute("class", "br.com.oncast.ontrack.shared.model.action.ReleaseCreateAction");
		}

		final List<Element> rollbackElements = getElements("//*[@class='br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction']/rollbackSubAction");
		for (final Element element : rollbackElements) {
			final Attribute attribute = element.attribute("class");
			if (attribute != null) continue;
			element.addAttribute("class", "br.com.oncast.ontrack.shared.model.action.ReleaseRemoveAction");
		}
	}
}
