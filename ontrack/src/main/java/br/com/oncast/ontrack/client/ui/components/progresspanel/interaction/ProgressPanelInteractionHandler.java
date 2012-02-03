package br.com.oncast.ontrack.client.ui.components.progresspanel.interaction;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnMoveAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseScopeUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ProgressPanelInteractionHandler implements ProgressPanelWidgetInteractionHandler {

	private ActionExecutionRequestHandler actionExecutionRequestHandler;
	private Release currentRelease;

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

	public void onKanbanColumnMove(final KanbanColumn column, final int index) {
		assureConfigured();
		this.actionExecutionRequestHandler.onUserActionExecutionRequest(new KanbanColumnMoveAction(currentRelease.getId(), column.getDescription(), index));
	}

	private void assureConfigured() {
		if (currentRelease == null) throw new RuntimeException(
				"This class was not yet configured.");
	}

	public void configureCurrentRelease(final Release release) {
		currentRelease = release;
	}
}