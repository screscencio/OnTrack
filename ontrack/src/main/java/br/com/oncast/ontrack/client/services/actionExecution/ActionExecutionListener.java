package br.com.oncast.ontrack.client.services.actionExecution;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public interface ActionExecutionListener {

	// TODO Verify the removal of the context from the methods signature.
	void onActionExecution(ModelAction action, ProjectContext context, final boolean wasRollback);

}
