package br.com.oncast.ontrack.client.services.actionExecution;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;

public interface ActionExecutionRequestHandler {

	void onActionExecutionRequest(ModelAction action);

	void onActionUndoRequest();

	void onActionRedoRequest();

}
