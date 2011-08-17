package br.com.oncast.ontrack.client.services.actionExecution;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;

public interface ActionExecutionRequestHandler {

	void onUserActionExecutionRequest(ModelAction action);

	void onUserActionUndoRequest();

	void onUserActionRedoRequest();

}
