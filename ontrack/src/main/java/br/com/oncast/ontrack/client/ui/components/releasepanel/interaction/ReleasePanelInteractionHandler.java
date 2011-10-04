package br.com.oncast.ontrack.client.ui.components.releasepanel.interaction;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.ComponentInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleasePanelWidgetInteractionHandler;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ReleaseRemoveAction;
import br.com.oncast.ontrack.shared.model.actions.ReleaseScopeUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.actions.ReleaseUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ReleasePanelInteractionHandler implements ReleasePanelWidgetInteractionHandler {

	private ActionExecutionRequestHandler applicationActionHandler;
	private ComponentInteractionHandler componentInteractionHandler;

	public void configureActionExecutionRequestHandler(final ActionExecutionRequestHandler actionExecutionRequestHandler) {
		this.applicationActionHandler = actionExecutionRequestHandler;
	}

	public void configureComponentInteractionHandler(final ComponentInteractionHandler componentInteractionHandler) {
		this.componentInteractionHandler = componentInteractionHandler;
	}

	@Override
	public void onReleaseDeletionRequest(final Release release) {
		assureConfigured();
		applicationActionHandler.onUserActionExecutionRequest(new ReleaseRemoveAction(release.getId()));
	}

	@Override
	public void onScopeIncreasePriorityRequest(final Scope scope) {
		assureConfigured();
		final Release release = scope.getRelease();
		applicationActionHandler.onUserActionExecutionRequest(new ReleaseScopeUpdatePriorityAction(scope.getId(),
				release.getScopeIndex(scope) - 1));
	}

	@Override
	public void onScopeDecreasePriorityRequest(final Scope scope) {
		assureConfigured();
		final Release release = scope.getRelease();
		applicationActionHandler.onUserActionExecutionRequest(new ReleaseScopeUpdatePriorityAction(scope.getId(),
				release.getScopeIndex(scope) + 1));
	}

	@Override
	public void onScopeChangePriorityRequest(final Scope scope, final Release targetRelease, final int newPriority) {
		assureConfigured();

		ModelAction action;

		final Release release = scope.getRelease();
		if (release.equals(targetRelease)) action = new ReleaseScopeUpdatePriorityAction(scope.getId(), newPriority);
		else action = new ScopeBindReleaseAction(scope.getId(), targetRelease.getFullDescription(), newPriority);

		applicationActionHandler.onUserActionExecutionRequest(action);
	}

	@Override
	public void onReleaseIncreasePriorityRequest(final Release release) {
		assureConfigured();
		applicationActionHandler.onUserActionExecutionRequest(new ReleaseUpdatePriorityAction(release.getId(),
				release.getParent().getChildIndex(release) - 1));
	}

	@Override
	public void onReleaseDecreasePriorityRequest(final Release release) {
		assureConfigured();
		applicationActionHandler.onUserActionExecutionRequest(new ReleaseUpdatePriorityAction(release.getId(),
				release.getParent().getChildIndex(release) + 1));
	}

	@Override
	public void onScopeSelectionRequest(final Scope scope) {
		assureConfigured();
		componentInteractionHandler.onScopeSelectionRequest(scope.getId());
	}

	private void assureConfigured() {
		if (applicationActionHandler == null || componentInteractionHandler == null) throw new RuntimeException("This class was not yet configured.");
	}
}
