package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd;

import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.KanbanScopeContainer;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ScopeWidgetContainer;
import br.com.oncast.ontrack.client.ui.components.scope.ScopeCardWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.ModelWidgetContainerDragHandler;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseScopeItemDragHandler extends ModelWidgetContainerDragHandler<Scope> {

	private final ItemDroppedListener itemDroppedListener;

	public ReleaseScopeItemDragHandler(final ItemDroppedListener itemDroppedListener) {
		this.itemDroppedListener = itemDroppedListener;
	}

	@Override
	public void onDragEnd(final DragEndEvent event) {
		super.onDragEnd(event); // IMPORTANT This keeps ModelWidgetContainer synchronized

		final ScopeCardWidget draggedScopeWidget = (ScopeCardWidget) event.getContext().draggable;
		final DropController dropTargetController = event.getContext().finalDropController;
		if (dropTargetController == null) return;

		final VerticalPanel targetReleaseArea = (VerticalPanel) dropTargetController.getDropTarget();
		final Widget parent = targetReleaseArea.getParent().getParent();
		if (parent instanceof ScopeWidgetContainer) {
			final ScopeWidgetContainer targetScopeContainer = (ScopeWidgetContainer) targetReleaseArea.getParent().getParent();
			itemDroppedListener.onItemDropped(draggedScopeWidget.getModelObject(), targetScopeContainer.getOwnerRelease(), targetReleaseArea.getWidgetIndex(draggedScopeWidget));
		} else if (parent instanceof KanbanScopeContainer) {
			final KanbanScopeContainer targetScopeContainer = (KanbanScopeContainer) targetReleaseArea.getParent().getParent();
			itemDroppedListener.onItemDropped(draggedScopeWidget.getScope(), targetScopeContainer.getKanbanColumn().getDescription());
		}
	}
}
