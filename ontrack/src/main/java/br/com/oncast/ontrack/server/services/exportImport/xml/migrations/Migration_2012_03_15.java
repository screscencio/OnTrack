package br.com.oncast.ontrack.server.services.exportImport.xml.migrations;

import org.dom4j.Element;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.Migration;

public class Migration_2012_03_15 extends Migration {

	private static final String SCOPE_BIND_RELEASE_ACTION = "br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction";

	@Override
	protected void execute() throws Exception {
		updateScopeBindRelease();
	}

	private void updateScopeBindRelease() {
		for (final Element scopeBindReleaseAction : getElementsWithClassAttribute(SCOPE_BIND_RELEASE_ACTION)) {

			final Element rollbackSubAction = scopeBindReleaseAction.element("rollbackSubAction");

			if (scopeBindReleaseAction.remove(rollbackSubAction)) {
				addRollbackSubActionList(scopeBindReleaseAction, rollbackSubAction);
			}
		}
	}

	private void addRollbackSubActionList(final Element scopeBindReleaseAction, final Element removedRollbackSubAction) {
		final Element referenceId = removedRollbackSubAction.element("referenceId");
		removedRollbackSubAction.remove(referenceId);

		final Element rollbackSubActionList = addListElementTo(scopeBindReleaseAction, "rollbackSubActions");
		addModelAction(rollbackSubActionList, getClass(removedRollbackSubAction), referenceId);
	}

	private void addModelAction(final Element subActionList, final String className, final Element referenceId) {
		final Element modelAction = addElementWithClassAttribute(subActionList, "modelAction", className);
		modelAction.add(referenceId.createCopy());
	}

}
