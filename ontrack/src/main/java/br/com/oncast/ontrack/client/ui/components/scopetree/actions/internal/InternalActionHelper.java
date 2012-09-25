package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

class InternalActionHelper {

	static ScopeTreeItem findScopeTreeItem(final ScopeTreeWidget tree, final Scope scope) throws UnableToCompleteActionException {
		try {
			return tree.findScopeTreeItem(scope);
		}
		catch (final ScopeNotFoundException e) {
			throw new UnableToCompleteActionException(ActionExecutionErrorMessageCode.TREE_ITEM_NOT_FOUND);
		}
	}
}
