package br.com.oncast.ontrack.client.services.actionExecution;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

// TODO +Now that this is a globally visible service, refactor this so that only the necessary methods are visible. A possible solution is to encapsulate the
// two
// interface implementations using composition (may be acceptable to use inner classes).
public class ActionExecutionService implements ActionExecutionRequestHandler, ActionExecutionListener {

	private final ActionExecutionManager actionManager;
	private final ContextProviderService contextService;
	private final List<ActionExecutionListener> actionExecutionListeners;

	public ActionExecutionService(final ContextProviderService contextService) {
		this.contextService = contextService;
		this.actionManager = new ActionExecutionManager(this);
		this.actionExecutionListeners = new ArrayList<ActionExecutionListener>();
	}

	@Override
	public void onActionExecutionRequest(final ModelAction action) {
		actionManager.doExecute(action, contextService.getProjectContext());
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
	public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet) {
		for (final ActionExecutionListener handler : actionExecutionListeners) {
			handler.onActionExecution(action, context, inferenceInfluencedScopeSet);
		}
	}

	public void addActionExecutionListener(final ActionExecutionListener actionExecutionListener) {
		if (this.actionExecutionListeners.contains(actionExecutionListener)) return;
		this.actionExecutionListeners.add(actionExecutionListener);
	}

	public void removeActionExecutionListener(final ActionExecutionListener actionExecutionListener) {
		this.actionExecutionListeners.remove(actionExecutionListener);
	}
}