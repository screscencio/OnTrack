package br.com.oncast.ontrack.client.ui.components.releasepanel;

import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.DraggableItemCreationListener;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.DropTargetCreationListener;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

// FIXME Add comments, explaining how to configure this class.
public class DragAndDropManager {

	private PickupDragController dragController;
	private DropTargetCreationListener dropTargetCreationHandler;
	private DraggableItemCreationListener draggableItemHandler;

	// FIXME Add comments
	public void configureBoundaryPanel(final AbsolutePanel boundaryPanel) {
		dragController = new PickupDragController(boundaryPanel, false);
		dragController.setBehaviorConstrainedToBoundaryPanel(true);
		dragController.setBehaviorMultipleSelection(false);
	}

	public DropTargetCreationListener getDropTargetCreationListener() {
		if (dropTargetCreationHandler == null) {
			dropTargetCreationHandler = new DropTargetCreationListener() {

				@Override
				public void onDropTargetCreated(final VerticalPanel dropTarget) {
					assureConfigured();

					final VerticalPanelDropController dropController = new VerticalPanelDropController(dropTarget);
					dragController.registerDropController(dropController);
				}
			};
		}

		return dropTargetCreationHandler;
	}

	public DraggableItemCreationListener getDraggableItemListener() {
		if (draggableItemHandler == null) {
			draggableItemHandler = new DraggableItemCreationListener() {
				@Override
				public void onDraggableItemCreated(final Widget widget, final Image draggableArea) {
					assureConfigured();
					dragController.makeDraggable(widget, draggableArea);
				}
			};
		}

		return draggableItemHandler;
	}

	private void assureConfigured() {
		if (dragController == null) throw new RuntimeException("The drag and drop manager must be configured before handling widget creation.");
	}

}
