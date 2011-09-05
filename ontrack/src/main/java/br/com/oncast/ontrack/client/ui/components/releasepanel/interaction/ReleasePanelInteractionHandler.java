package br.com.oncast.ontrack.client.ui.components.releasepanel.interaction;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleasePanelWidgetInteractionHandler;
import br.com.oncast.ontrack.shared.model.actions.ReleaseRemoveAction;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ReleasePanelInteractionHandler implements ReleasePanelWidgetInteractionHandler {

	private ActionExecutionRequestHandler applicationActionHandler;

	public void configure(final ActionExecutionRequestHandler actionExecutionRequestHandler) {
		this.applicationActionHandler = actionExecutionRequestHandler;
	}

	@Override
	public void onReleaseDeletionRequest(final Release release) {
		assureConfigured();
		applicationActionHandler.onUserActionExecutionRequest(new ReleaseRemoveAction(release.getId()));
	}

	@Override
	public void onScopeIncreasePriorityRequest(final Scope scope) {
		assureConfigured();
		// FIXME Create a new ScopeIncreasePriorityAction.
		// applicationActionHandler.onUserActionExecutionRequest(new ScopeIncreasePriorityAction(scope.getId()));
	}

	@Override
	public void onScopeDecreasePriorityRequest(final Scope scope) {
		assureConfigured();
		// FIXME Create a new ScopeDecreasePriorityAction.
		// applicationActionHandler.onUserActionExecutionRequest(new ScopeDecreasePriorityAction(scope.getId()));
	}

	private void assureConfigured() {
		if (applicationActionHandler == null) throw new RuntimeException("This class was not yet configured.");
	}

}
