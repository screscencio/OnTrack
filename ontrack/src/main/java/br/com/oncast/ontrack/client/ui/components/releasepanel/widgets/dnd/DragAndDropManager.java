package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;
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

	private DropTargetCreationListener dropTargetCreationHandler;
	private DraggableItemCreationListener draggableItemHandler;

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

	/**
	 * Returns a listener that have to be notified when a new drop target is created, so this manager can
	 * configure it to receive (by drop) draggable items.
	 */
	public DropTargetCreationListener getDropTargetCreationListener() {
		if (dropTargetCreationHandler == null) {
			dropTargetCreationHandler = new DropTargetCreationListener() {

				@Override
				public void onDropTargetCreated(final VerticalPanel dropTarget) {
					assureConfigured();
					dragController.registerDropController(new VerticalPanelDropController(dropTarget));
				}
			};
		}

		return dropTargetCreationHandler;
	}

	/**
	 * Returns a listener that have to be notified when a new draggable item is created, so this manager can
	 * configure it and allow it to be dropped in a previously defined droppable area,
	 * using {@link DragAndDropManager#getDropTargetCreationListener()} method.
	 */
	public DraggableItemCreationListener getDraggableItemCreationListener() {
		if (draggableItemHandler == null) {
			draggableItemHandler = new DraggableItemCreationListener() {
				@Override
				public void onDraggableItemCreated(final Widget draggableWidget, final Widget draggableAreaWidget) {
					assureConfigured();
					dragController.makeDraggable(draggableWidget, draggableAreaWidget);
				}
			};
		}

		return draggableItemHandler;
	}

	private void assureConfigured() {
		if (dragController == null) throw new RuntimeException("The drag and drop manager must be configured before handling widget creation.");
	}
}
