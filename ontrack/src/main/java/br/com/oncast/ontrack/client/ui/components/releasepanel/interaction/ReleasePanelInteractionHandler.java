package br.com.oncast.ontrack.client.ui.components.releasepanel.interaction;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleasePanelWidgetInteractionHandler;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRenameAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseScopeUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ReleasePanelInteractionHandler implements ReleasePanelWidgetInteractionHandler {

	private ActionExecutionRequestHandler applicationActionHandler;

	public void configureActionExecutionRequestHandler(final ActionExecutionRequestHandler actionExecutionRequestHandler) {
		this.applicationActionHandler = actionExecutionRequestHandler;
	}

	public void deconfigure() {
		this.applicationActionHandler = null;
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
		applicationActionHandler.onUserActionExecutionRequest(new ReleaseScopeUpdatePriorityAction(release.getId(), scope.getId(),
				release.getScopeIndex(scope) - 1));
	}

	@Override
	public void onScopeDecreasePriorityRequest(final Scope scope) {
		assureConfigured();
		final Release release = scope.getRelease();
		applicationActionHandler.onUserActionExecutionRequest(new ReleaseScopeUpdatePriorityAction(release.getId(), scope.getId(),
				release.getScopeIndex(scope) + 1));
	}

	@Override
	public void onScopeDragAndDropRequest(final Scope scope, final Release targetRelease, final int newPriority) {
		assureConfigured();

		ModelAction action;

		final Release release = scope.getRelease();
		if (release.equals(targetRelease)) action = new ReleaseScopeUpdatePriorityAction(release.getId(), scope.getId(), newPriority);
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
	public void onReleaseRenameRequest(final Release release, final String newReleaseName) {
		assureConfigured();
		applicationActionHandler.onUserActionExecutionRequest(new ReleaseRenameAction(release.getId(), newReleaseName));
	}

	private void assureConfigured() {
		if (applicationActionHandler == null) throw new RuntimeException(
				"This class was not yet configured.");
	}

	@Override
	public void onScopeUnderworkdDropRequest(final Scope scope) {
		assureConfigured();
		applicationActionHandler.onUserActionExecutionRequest(new ScopeDeclareProgressAction(scope.getId(), ProgressState.UNDER_WORK.getDescription()));
	}
}
