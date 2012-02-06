package br.com.oncast.ontrack.client.ui.components.progresspanel.interaction;

import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public interface ProgressPanelWidgetInteractionHandler {

	void onDragAndDropPriorityRequest(Scope scope, int newPriority);

	void onDragAndDropProgressRequest(Scope scope, String newProgress);

	void onKanbanColumnMove(final KanbanColumn column, final int index);

	void onKanbanColumnRemove(final KanbanColumn column);

	void onKanbanColumnRename(KanbanColumn column, String newDescription);
}
