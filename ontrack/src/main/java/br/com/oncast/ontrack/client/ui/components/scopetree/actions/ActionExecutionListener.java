package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;

public interface ActionExecutionListener {

	void onActionExecution(ScopeAction action, final boolean wasRollback);

}
