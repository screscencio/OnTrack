package br.com.oncast.ontrack.client.services.actions;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;

// TODO Now that this is a globally visible service, refactor this so that only the necessary methods are visible. A possible solution is to encapsulate the two
// interface implementations using composition (may be acceptable to use inner classes).
public class ActionExecutionService implements ActionExecutionRequestHandler, ActionExecutionListener {

	private final ActionExecutionManager actionManager;
	private final ContextProviderService contextService;
	private final List<ActionExecutionListener> actionExecutionSuccessHandlers;

	public ActionExecutionService(final ContextProviderService contextService) {
		this.contextService = contextService;
		this.actionManager = new ActionExecutionManager(this);
		this.actionExecutionSuccessHandlers = new ArrayList<ActionExecutionListener>();
	}

	@Override
	public void onActionExecutionRequest(final ScopeAction action) {
		actionManager.execute(action, contextService.getProjectContext());
	}

	@Override
	public void onActionUndoRequest() {
		actionManager.undo(contextService.getProjectContext());
	}

	@Override
	public void onActionRedoRequest() {
		actionManager.redo(contextService.getProjectContext());
	}

	@Override
	public void onActionExecution(final ScopeAction action, final ProjectContext context, final boolean wasRollback) {
		for (final ActionExecutionListener handler : actionExecutionSuccessHandlers) {
			handler.onActionExecution(action, context, wasRollback);
		}
	}

	public void addActionExecutionListener(final ActionExecutionListener actionExecutionSuccessListener) {
		if (this.actionExecutionSuccessHandlers.contains(actionExecutionSuccessListener)) return;
		this.actionExecutionSuccessHandlers.add(actionExecutionSuccessListener);
	}

	public void removeActionExecutionListener(final ActionExecutionListener actionExecutionSuccessListener) {
		this.actionExecutionSuccessHandlers.remove(actionExecutionSuccessListener);
	}
}