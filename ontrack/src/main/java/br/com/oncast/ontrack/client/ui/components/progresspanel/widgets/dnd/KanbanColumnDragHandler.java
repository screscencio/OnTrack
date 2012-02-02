package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.dnd;

import br.com.oncast.ontrack.client.ui.components.progresspanel.interaction.ProgressPanelInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.KanbanColumnWidget;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandlerAdapter;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class KanbanColumnDragHandler extends DragHandlerAdapter {

	private final ProgressPanelInteractionHandler interactionHandler;

	public KanbanColumnDragHandler(final ProgressPanelInteractionHandler interactionHandler) {
		this.interactionHandler = interactionHandler;
	}

	@Override
	public void onDragEnd(final DragEndEvent event) {
		final DropController finalDropController = event.getContext().finalDropController;
		if (finalDropController == null) return;

		final HorizontalPanel board = (HorizontalPanel) finalDropController.getDropTarget();
		final KanbanColumnWidget column = (KanbanColumnWidget) event.getContext().draggable;

		final int requestedIndex = board.getWidgetIndex(column);

		interactionHandler.onKanbanColumnMove(column.getKanbanColumn(), requestedIndex);
	}

}
