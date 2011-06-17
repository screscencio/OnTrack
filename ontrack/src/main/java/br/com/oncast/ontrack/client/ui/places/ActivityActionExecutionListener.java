package br.com.oncast.ontrack.client.ui.places;

import java.util.List;

import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionListener;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeAction;

public class ActivityActionExecutionListener implements ActionExecutionListener {
	private List<ActionExecutionListener> actionExecutionSuccessListeners;

	@Override
	public void onActionExecution(final ScopeAction action, final ProjectContext context, final boolean wasRollback) {
		if (actionExecutionSuccessListeners == null) return;
		for (final ActionExecutionListener listener : actionExecutionSuccessListeners)
			listener.onActionExecution(action, context, wasRollback);
	}

	public void setActionExecutionListeners(final List<ActionExecutionListener> actionExecutionSuccessListeners) {
		this.actionExecutionSuccessListeners = actionExecutionSuccessListeners;
	}
}
