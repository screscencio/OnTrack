package br.com.oncast.ontrack.client.ui.places;

import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ActivityActionExecutionListener implements ActionExecutionListener {

	private List<ActionExecutionListener> actionExecutionSuccessListeners;
	private final ClientErrorMessages messages;

	public ActivityActionExecutionListener(final ClientErrorMessages messages) {
		this.messages = messages;
	}

	@Override
	public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
			final Set<UUID> inferenceInfluencedScopeSet, final boolean isUserAction) {
		if (action instanceof TeamInviteAction) {
			ClientServiceProvider.getInstance().getClientAlertingService()
					.showInfo(messages.acceptedInvitation(action.getReferenceId().toStringRepresentation()));
		}
		if (actionExecutionSuccessListeners == null) return;
		for (final ActionExecutionListener listener : actionExecutionSuccessListeners)
			listener.onActionExecution(action, context, actionContext, inferenceInfluencedScopeSet, isUserAction);
	}

	public void setActionExecutionListeners(final List<ActionExecutionListener> actionExecutionSuccessListeners) {
		this.actionExecutionSuccessListeners = actionExecutionSuccessListeners;
	}
}
