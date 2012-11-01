package br.com.oncast.ontrack.client.ui.components.progresspanel.interaction;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnCreateAction;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnMoveAction;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnRemoveAction;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnRenameAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ProgressPanelInteractionHandler implements ProgressPanelWidgetInteractionHandler {

	private ActionExecutionRequestHandler actionExecutionRequestHandler;
	private final Release currentRelease;

	public ProgressPanelInteractionHandler(final Release release) {
		currentRelease = release;
	}

	@Override
	public void onDragAndDropPriorityRequest(final Scope scope, final int newPriority) {}

	@Override
	public void onDragAndDropProgressRequest(final Scope scope, final String newProgress) {
		actionExecutionRequestHandler.onUserActionExecutionRequest(new ScopeDeclareProgressAction(scope.getId(), newProgress));
	}

	@Override
	public void onKanbanColumnMove(final KanbanColumn column, final int index) {
		this.actionExecutionRequestHandler.onUserActionExecutionRequest(new KanbanColumnMoveAction(currentRelease.getId(), column.getDescription(), index));
	}

	@Override
	public void onKanbanColumnRemove(final KanbanColumn column) {
		this.actionExecutionRequestHandler.onUserActionExecutionRequest(new KanbanColumnRemoveAction(currentRelease.getId(), column.getDescription()));
	}

	@Override
	public void onKanbanColumnRename(final KanbanColumn column, final String newDescription) {
		this.actionExecutionRequestHandler.onUserActionExecutionRequest(new KanbanColumnRenameAction(currentRelease.getId(), column.getDescription(),
				newDescription));
	}

	@Override
	public void onKanbanColumnCreate(final String description, final int index) {
		this.actionExecutionRequestHandler.onUserActionExecutionRequest(new KanbanColumnCreateAction(currentRelease.getId(), description, true, index));
	}

	public void configureActionExecutionRequestHandler(final ActionExecutionRequestHandler actionExecutionRequestHandler) {
		this.actionExecutionRequestHandler = actionExecutionRequestHandler;
	}
}