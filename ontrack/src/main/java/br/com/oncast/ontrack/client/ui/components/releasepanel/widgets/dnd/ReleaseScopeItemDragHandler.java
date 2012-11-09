package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd;

import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ScopeWidget;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ScopeWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.ModelWidgetContainerDragHandler;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ReleaseScopeItemDragHandler extends ModelWidgetContainerDragHandler<Scope> {

	private final ItemDroppedListener itemDroppedListener;

	public ReleaseScopeItemDragHandler(final ItemDroppedListener itemDroppedListener) {
		this.itemDroppedListener = itemDroppedListener;
	}

	@Override
	public void onDragEnd(final DragEndEvent event) {
		super.onDragEnd(event); // IMPORTANT This keeps ModelWidgetContainer synchronized

		final ScopeWidget draggedScopeWidget = (ScopeWidget) event.getContext().draggable;
		final DropController dropTargetController = event.getContext().finalDropController;
		if (dropTargetController == null) return;

		if (dropTargetController.getDropTarget() instanceof VerticalPanel) {
			final VerticalPanel targetReleaseArea = (VerticalPanel) dropTargetController.getDropTarget();
			final ScopeWidgetContainer targetScopeContainer = (ScopeWidgetContainer) targetReleaseArea.getParent().getParent();

			itemDroppedListener.onItemDropped(draggedScopeWidget.getModelObject(), targetScopeContainer.getOwnerRelease(),
					targetReleaseArea.getWidgetIndex(draggedScopeWidget));
		}
		else itemDroppedListener.onItemDropped(draggedScopeWidget.getScope());
	}
}
