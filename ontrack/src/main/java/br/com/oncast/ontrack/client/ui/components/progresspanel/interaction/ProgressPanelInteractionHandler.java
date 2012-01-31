package br.com.oncast.ontrack.client.ui.components.progresspanel.interaction;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.shared.model.action.ReleaseScopeUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ProgressPanelInteractionHandler implements ProgressPanelWidgetInteractionHandler {

	private ActionExecutionRequestHandler actionExecutionRequestHandler;

	@Override
	public void onDragAndDropPriorityRequest(final Scope scope, final int newPriority) {
		actionExecutionRequestHandler.onUserActionExecutionRequest(new ReleaseScopeUpdatePriorityAction(scope.getRelease().getId(),
				scope.getId(), newPriority));
	}

	@Override
	public void onDragAndDropProgressRequest(final Scope scope, final String newProgress) {
		actionExecutionRequestHandler.onUserActionExecutionRequest(new ScopeDeclareProgressAction(scope.getId(), newProgress));

	}

	public void configureActionExecutionRequestHandler(final ActionExecutionRequestHandler actionExecutionRequestHandler) {
		this.actionExecutionRequestHandler = actionExecutionRequestHandler;
	}
}