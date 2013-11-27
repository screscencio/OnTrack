package br.com.oncast.ontrack.client.ui.places;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import java.util.List;

public class ActivityActionExecutionListener implements ActionExecutionListener {

	private List<ActionExecutionListener> actionExecutionSuccessListeners;

	public ActivityActionExecutionListener() {}

	@Override
	public void onActionExecution(final ActionExecutionContext execution, final ProjectContext context, final boolean isUserAction) {
		if (actionExecutionSuccessListeners == null) return;
		for (final ActionExecutionListener listener : actionExecutionSuccessListeners)
			listener.onActionExecution(execution, context, isUserAction);
	}

	public void setActionExecutionListeners(final List<ActionExecutionListener> actionExecutionSuccessListeners) {
		this.actionExecutionSuccessListeners = actionExecutionSuccessListeners;
	}
}
