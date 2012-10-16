package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.Scope;

class InternalActionHelper {

	// TODO inline this method after validated that findScopeTreeItem is working properly
	static ScopeTreeItem findScopeTreeItem(final ScopeTreeWidget tree, final Scope scope) throws UnableToCompleteActionException {
		return tree.findScopeTreeItem(scope);
	}
}
