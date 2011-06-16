package br.com.oncast.ontrack.client.ui.components.scopetree.actions;

import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;

public interface ActionExecutionListener {

	// TODO Verify the removal of the context from the methods signature.
	void onActionExecution(ScopeAction action, ProjectContext context, final boolean wasRollback);

}
