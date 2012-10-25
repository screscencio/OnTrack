package br.com.oncast.ontrack.client.ui.generalwidgets.dnd;

import java.util.Map;

import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Configure draggable items and the target place to where these items can be dropped.
 * The {@link DragAndDropManager#configureBoundaryPanel(AbsolutePanel)} method have to be called before
 * executing the provided listeners.
 */
public class DragAndDropManager {

	private PickupDragController dragController;

	private final Map<CellPanel, DropController> dropTargetMap = new java.util.HashMap<CellPanel, DropController>();

	/**
	 * Configure a draggable area (boundaryPanel) on which the draggable items can be moved over.
	 */
	public void configureBoundaryPanel(final AbsolutePanel boundaryPanel) {
		dragController = new PickupDragController(boundaryPanel, false);
		dragController.setBehaviorConstrainedToBoundaryPanel(true);
		dragController.setBehaviorMultipleSelection(false);
	}

	public void setDragHandler(final DragHandler dragHandler) {
		dragController.addDragHandler(dragHandler);
	}

	private void assureConfigured() {
		if (dragController == null) throw new RuntimeException("The drag and drop manager must be configured before handling widget creation.");
	}

	public void monitorDropTarget(final CellPanel panel, final DropControllerFactory factory) {
		panel.addAttachHandler(new Handler() {

			@Override
			public void onAttachOrDetach(final AttachEvent event) {
				assureConfigured();

				if (event.isAttached()) {
					final DropController dropController = factory.create(panel);
					dropTargetMap.put(panel, dropController);
					dragController.registerDropController(dropController);
				}
				else {
					dragController.unregisterDropController(dropTargetMap.get(panel));
					dropTargetMap.remove(panel);
				}
			}
		});
	}

	public void monitorNewDraggableItem(final Widget draggableWidget, final Widget draggableAreaWidget) {
		assureConfigured();
		dragController.makeDraggable(draggableWidget, draggableAreaWidget);
	}

	public void addDropHandler(final DropController controller) {
		dragController.registerDropController(controller);
	}
}
