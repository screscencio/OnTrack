package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.dnd;

import br.com.oncast.ontrack.client.ui.components.progresspanel.interaction.ProgressPanelWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.KanbanScopeContainer;
import br.com.oncast.ontrack.client.ui.components.scope.ScopeCardWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.ModelWidgetContainerDragHandler;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.progress.ProgressState;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.VerticalPanel;

public class KanbanScopeItemDragHandler extends ModelWidgetContainerDragHandler<Scope> {

	private final ProgressPanelWidgetInteractionHandler interactionHandler;

	public KanbanScopeItemDragHandler(final ProgressPanelWidgetInteractionHandler interactionHandler) {
		this.interactionHandler = interactionHandler;
	}

	@Override
	public void onDragEnd(final DragEndEvent event) {
		super.onDragEnd(event); // IMPORTANT This keeps ModelWidgetContainer synchronized

		final DropController finalDropController = event.getContext().finalDropController;
		if (finalDropController == null) return;

		final VerticalPanel dropTarget = (VerticalPanel) finalDropController.getDropTarget();
		final ScopeCardWidget draggedScope = (ScopeCardWidget) event.getContext().draggable;
		final KanbanScopeContainer scopeContainer = (KanbanScopeContainer) dropTarget.getParent().getParent();
		final KanbanColumn kanbanColumn = scopeContainer.getKanbanColumn();

		if (!isPriorityChange(draggedScope.getModelObject(), kanbanColumn.getDescription())) {
			interactionHandler.onDragAndDropProgressRequest(draggedScope.getModelObject(), kanbanColumn.getDescription());
		}
	}

	private boolean isPriorityChange(final Scope scope, final String title) {
		if (scope.getProgress().getState() == ProgressState.NOT_STARTED && ProgressState.getStateForDescription(title) == ProgressState.NOT_STARTED) return true;
		return scope.getProgress().getDescription().equals(title);
	}
}
