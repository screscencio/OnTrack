package br.com.oncast.ontrack.client.ui.places.planning;

import java.util.List;

import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionManager;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;

public class PlanningActionExecutionRequestHandler implements ActionExecutionRequestHandler, ActionExecutionListener {

	private final ActionManager actionManager;
	private final List<ActionExecutionListener> actionExecutionSuccessHandlers;
	private final ProjectContext projectContext;

	public PlanningActionExecutionRequestHandler(final ProjectContext projectContext, final List<ActionExecutionListener> actionExecutionSuccessHandlers) {
		this.projectContext = projectContext;
		actionManager = new ActionManager(this);
		this.actionExecutionSuccessHandlers = actionExecutionSuccessHandlers;
	}

	@Override
	public void onActionExecutionRequest(final ScopeAction action) {
		actionManager.execute(action, projectContext);
	}

	@Override
	public void onActionUndoRequest() {
		actionManager.undo(projectContext);
	}

	@Override
	public void onActionRedoRequest() {
		actionManager.redo(projectContext);
	}

	@Override
	public void onActionExecution(final ScopeAction action, final boolean wasRollback) {
		for (final ActionExecutionListener handler : actionExecutionSuccessHandlers) {
			handler.onActionExecution(action, wasRollback);
		}
	}
}