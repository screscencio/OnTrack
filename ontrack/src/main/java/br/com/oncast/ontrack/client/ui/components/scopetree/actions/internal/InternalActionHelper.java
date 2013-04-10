package br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;

class InternalActionHelper {

	// TODO inline this method after validated that findScopeTreeItem is working properly
	static ScopeTreeItem findScopeTreeItem(final ScopeTreeWidget tree, final Scope scope) throws UnableToCompleteActionException {
		return tree.findScopeTreeItem(scope);
	}

	static UserRepresentation findCurrentUser() throws UnableToCompleteActionException {
		try {
			return ClientServices.getCurrentProjectContext().findUser(ClientServices.getCurrentUser());
		}
		catch (final UserNotFoundException e) {
			throw new UnableToCompleteActionException(e);
		}
	}
}
