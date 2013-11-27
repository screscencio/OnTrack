package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import static br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InternalActionHelper.findScopeTreeItem;

public class DeclareImpedimentInternalAction implements OneStepInternalAction {

	private final Scope scope;

	public DeclareImpedimentInternalAction(final Scope scope) {
		this.scope = scope;
	}

	@Override
	public void execute(final ScopeTreeWidget tree) throws UnableToCompleteActionException {
		findScopeTreeItem(tree, scope).getScopeTreeItemWidget().showImpedimentMenu();
	}
}
