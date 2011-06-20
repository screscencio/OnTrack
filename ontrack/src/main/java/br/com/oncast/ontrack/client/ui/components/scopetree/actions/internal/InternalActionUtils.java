package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

class InternalActionUtils {

	static ScopeTreeItem findScopeTreeItem(final ScopeTreeWidget tree, final Scope scope) throws UnableToCompleteActionException {
		try {
			return tree.findScopeTreeItem(scope.getId());
		}
		catch (final ScopeNotFoundException e) {
			throw new UnableToCompleteActionException(
					"The internal insertion action execution was not able to be completed successfuly: It was not possible to find the desired TreeItem.", e);
		}
	}
}
