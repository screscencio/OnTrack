package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd;

import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ScopeWidget;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ScopeWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragHandlerAdapter;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ReleaseScopeItemDragHandler extends DragHandlerAdapter {

	private final ItemDroppedListener itemDroppedListener;

	public ReleaseScopeItemDragHandler(final ItemDroppedListener itemDroppedListener) {
		this.itemDroppedListener = itemDroppedListener;
	}

	@Override
	public void onDragEnd(final DragEndEvent event) {
		final ScopeWidget draggedScopeWidget = (ScopeWidget) event.getContext().draggable;
		final DropController dropTargetController = event.getContext().finalDropController;
		if (isDropTargetInvalid(dropTargetController)) return;

		final VerticalPanel targetReleaseArea = (VerticalPanel) dropTargetController.getDropTarget();
		final ScopeWidgetContainer targetScopeContainer = (ScopeWidgetContainer) targetReleaseArea.getParent();

		targetScopeContainer.addToWidgetMapping(draggedScopeWidget.getScope(), draggedScopeWidget);

		itemDroppedListener.onItemDropped(draggedScopeWidget.getModelObject(), targetScopeContainer.getOwnerRelease(),
				targetReleaseArea.getWidgetIndex(draggedScopeWidget));

	}

	private boolean isDropTargetInvalid(final DropController dropController) {
		return dropController == null;
	}

}
