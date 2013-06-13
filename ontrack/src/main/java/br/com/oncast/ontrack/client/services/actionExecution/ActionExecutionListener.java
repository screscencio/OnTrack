package br.com.oncast.ontrack.client.services.actionExecution;


import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

public interface ActionExecutionListener {

	// TODO +Verify the removal of the context from the methods signature.
	void onActionExecution(ModelAction action, ProjectContext context, ActionContext actionContext, ActionExecutionContext executionContext, boolean isUserAction);

}
