package br.com.oncast.ontrack.client.ui.places;

import java.util.List;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

public class ActivityActionExecutionListener implements ActionExecutionListener {

	private List<ActionExecutionListener> actionExecutionSuccessListeners;

	public ActivityActionExecutionListener() {}

	@Override
	public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
			final ActionExecutionContext executionContext, final boolean isUserAction) {
		if (actionExecutionSuccessListeners == null) return;
		for (final ActionExecutionListener listener : actionExecutionSuccessListeners)
			listener.onActionExecution(action, context, actionContext, executionContext, isUserAction);
	}

	public void setActionExecutionListeners(final List<ActionExecutionListener> actionExecutionSuccessListeners) {
		this.actionExecutionSuccessListeners = actionExecutionSuccessListeners;
	}
}
