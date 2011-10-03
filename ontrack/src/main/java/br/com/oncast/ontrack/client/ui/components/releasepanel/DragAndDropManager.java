package br.com.oncast.ontrack.client.ui.components.releasepanel;

import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd.DraggableItemCreationListener;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd.DropTargetCreationListener;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
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
		dragController.addDragHandler(new DragHandler() {

			@Override
			public void onPreviewDragStart(final DragStartEvent event) throws VetoDragException {
				// FIXME Auto-generated catch block

			}

			@Override
			public void onPreviewDragEnd(final DragEndEvent event) throws VetoDragException {
				// FIXME Auto-generated catch block

			}

			@Override
			public void onDragStart(final DragStartEvent event) {
				// FIXME Auto-generated catch block

			}

			@Override
			public void onDragEnd(final DragEndEvent event) {
				// FIXME Auto-generated catch block
			}
		});
	}

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
