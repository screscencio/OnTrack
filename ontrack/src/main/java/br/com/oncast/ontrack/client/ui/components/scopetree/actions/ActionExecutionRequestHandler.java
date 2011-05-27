package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;

public interface ActionExecutionRequestHandler {

	void onActionExecutionRequest(ScopeAction action);

	void onActionUndoRequest();

	void onActionRedoRequest();

}
