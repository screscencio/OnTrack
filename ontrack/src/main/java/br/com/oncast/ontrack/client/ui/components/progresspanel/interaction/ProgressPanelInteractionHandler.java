package br.com.oncast.ontrack.client.ui.components.progresspanel.interaction;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnCreateAction;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnMoveAction;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnRemoveAction;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnRenameAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ProgressPanelInteractionHandler implements ProgressPanelWidgetInteractionHandler {

	private ActionExecutionRequestHandler actionExecutionRequestHandler;
	private final Release release;
	private final Kanban kanban;

	public ProgressPanelInteractionHandler(final Release release, final Kanban kanban) {
		this.release = release;
		this.kanban = kanban;
	}

	@Override
	public void onDragAndDropPriorityRequest(final Scope scope, final int newPriority) {}

	@Override
	public void onDragAndDropProgressRequest(final Scope scope, final String newProgress) {
		actionExecutionRequestHandler.onUserActionExecutionRequest(new ScopeDeclareProgressAction(scope.getId(), newProgress));
	}

	@Override
	public void onKanbanColumnMove(final KanbanColumn column, final int index) {
		this.actionExecutionRequestHandler.onUserActionExecutionRequest(new KanbanColumnMoveAction(release.getId(), column.getDescription(), index));
	}

	@Override
	public void onKanbanColumnRemove(final KanbanColumn column) {
		this.actionExecutionRequestHandler.onUserActionExecutionRequest(new KanbanColumnRemoveAction(release.getId(), column.getDescription()));
	}

	@Override
	public void onKanbanColumnRename(final KanbanColumn column, final String newDescription) {
		this.actionExecutionRequestHandler.onUserActionExecutionRequest(new KanbanColumnRenameAction(release.getId(), column.getDescription(),
				newDescription));
	}

	@Override
	public void onKanbanColumnCreate(final String description, final String previousColumnDescription) {
		this.actionExecutionRequestHandler.onUserActionExecutionRequest(new KanbanColumnCreateAction(release.getId(), description, true, kanban
				.indexOf(previousColumnDescription) + 1));
	}

	public void configureActionExecutionRequestHandler(final ActionExecutionRequestHandler actionExecutionRequestHandler) {
		this.actionExecutionRequestHandler = actionExecutionRequestHandler;
	}
}