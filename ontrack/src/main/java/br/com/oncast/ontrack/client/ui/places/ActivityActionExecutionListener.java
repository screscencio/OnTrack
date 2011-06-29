package br.com.oncast.ontrack.client.ui.places;

import java.util.List;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public class ActivityActionExecutionListener implements ActionExecutionListener {
	private List<ActionExecutionListener> actionExecutionSuccessListeners;

	@Override
	public void onActionExecution(final ModelAction action, final ProjectContext context) {
		if (actionExecutionSuccessListeners == null) return;
		for (final ActionExecutionListener listener : actionExecutionSuccessListeners)
			listener.onActionExecution(action, context);
	}

	public void setActionExecutionListeners(final List<ActionExecutionListener> actionExecutionSuccessListeners) {
		this.actionExecutionSuccessListeners = actionExecutionSuccessListeners;
	}
}
