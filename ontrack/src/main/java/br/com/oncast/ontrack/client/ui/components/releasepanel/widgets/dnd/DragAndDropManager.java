package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd;

import java.util.Map;

import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleaseWidget;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Configure draggable items and the target place to where these items can be dropped.
 * The {@link DragAndDropManager#configureBoundaryPanel(AbsolutePanel)} method have to be called before
 * executing the provided listeners.
 */
public class DragAndDropManager {

	private PickupDragController dragController;

	private final Map<VerticalPanel, VerticalPanelDropController> dropTargetMap = new java.util.HashMap<VerticalPanel, VerticalPanelDropController>();

	/**
	 * Configure a draggable area (boundaryPanel) on which the draggable items can be moved over.
	 */
	public void configureBoundaryPanel(final AbsolutePanel boundaryPanel) {
		dragController = new PickupDragController(boundaryPanel, false);
		dragController.setBehaviorConstrainedToBoundaryPanel(true);
		dragController.setBehaviorMultipleSelection(false);
	}

	public void setDragHandler(final ScopeItemDragHandler scopeItemDragHandler) {
		dragController.addDragHandler(scopeItemDragHandler);
	}

	private void assureConfigured() {
		if (dragController == null) throw new RuntimeException("The drag and drop manager must be configured before handling widget creation.");
	}

	public void monitorDropTarget(final ReleaseWidget widget) {
		final VerticalPanel dropTarget = widget.getScopeContainer().getVerticalContainer();
		dropTarget.addAttachHandler(new Handler() {

			@Override
			public void onAttachOrDetach(final AttachEvent event) {
				assureConfigured();

				if (event.isAttached()) {
					final VerticalPanelDropController dropController = new VerticalPanelDropController(dropTarget);
					dropTargetMap.put(dropTarget, dropController);
					dragController.registerDropController(dropController);
				}
				else {
					dragController.unregisterDropController(dropTargetMap.get(dropTarget));
					dropTargetMap.remove(dropTarget);
				}
			}
		});
	}

	public void monitorNewDraggableItem(final Widget draggableWidget, final Widget draggableAreaWidget) {
		assureConfigured();
		dragController.makeDraggable(draggableWidget, draggableAreaWidget);
	}
}
