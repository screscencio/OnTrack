package br.com.oncast.ontrack.client.ui.places;

import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ActivityActionExecutionListener implements ActionExecutionListener {
	private List<ActionExecutionListener> actionExecutionSuccessListeners;

	@Override
	public void onActionExecution(final ModelAction action, final ProjectContext context, ActionContext actionContext,
			final Set<UUID> inferenceInfluencedScopeSet, final boolean isUserAction) {
		if (action instanceof TeamInviteAction) {
			ClientServiceProvider.getInstance().getClientNotificationService()
					.showInfo("The User '" + action.getReferenceId().toStringRepresentation() + "' accepted the invitaton for this project");
		}
		if (actionExecutionSuccessListeners == null) return;
		for (final ActionExecutionListener listener : actionExecutionSuccessListeners)
			listener.onActionExecution(action, context, actionContext, inferenceInfluencedScopeSet, isUserAction);
	}

	public void setActionExecutionListeners(final List<ActionExecutionListener> actionExecutionSuccessListeners) {
		this.actionExecutionSuccessListeners = actionExecutionSuccessListeners;
	}
}
