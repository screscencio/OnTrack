package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.dnd;

import br.com.oncast.ontrack.client.ui.components.progresspanel.interaction.ProgressPanelInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.KanbanColumnWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragHandlerAdapter;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
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

		final KanbanColumnWidget column = (KanbanColumnWidget) event.getContext().draggable;
		final int requestedIndex = getDropIndex(finalDropController, column);

		interactionHandler.onKanbanColumnMove(column.getKanbanColumn(), requestedIndex);
	}

	private int getDropIndex(final DropController finalDropController, final KanbanColumnWidget column) {
		return getBoard(finalDropController).getWidgetIndex(column);
	}

	private HorizontalPanel getBoard(final DropController finalDropController) {
		return (HorizontalPanel) finalDropController.getDropTarget();
	}
}
