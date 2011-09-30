package dnd.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class Gwt_dnd_test implements EntryPoint {

	/**
	   * Main entry point method.
	   */
	  public void onModuleLoad() {
		// Create a boundary panel to constrain all drag operations
		    AbsolutePanel boundaryPanel = new AbsolutePanel();
		    boundaryPanel.setPixelSize(400, 300);
		    boundaryPanel.addStyleName("getting-started-blue");

		    // Create a drop target on which we can drop labels
		    AbsolutePanel targetPanel = new AbsolutePanel();
		    targetPanel.setPixelSize(300, 200);
		    targetPanel.addStyleName("getting-started-blue");

		    // Add both panels to the root panel
		    RootPanel.get().add(boundaryPanel);
		    boundaryPanel.add(targetPanel, 40, 40);

		    // Create a DragController for each logical area where a set of draggable
		    // widgets and drop targets will be allowed to interact with one another.
		    PickupDragController dragController = new PickupDragController(boundaryPanel, true);

		    // Positioner is always constrained to the boundary panel
		    // Use 'true' to also constrain the draggable or drag proxy to the boundary panel
		    dragController.setBehaviorConstrainedToBoundaryPanel(false);

		    // Allow multiple widgets to be selected at once using CTRL-click
		    dragController.setBehaviorMultipleSelection(true);

		    // create a DropController for each drop target on which draggable widgets
		    // can be dropped
		    DropController dropController = new AbsolutePositionDropController(targetPanel);

		    // Don't forget to register each DropController with a DragController
		    dragController.registerDropController(dropController);

		    // create a few randomly placed draggable labels
		    for (int i = 1; i <= 5; i++) {
		      // create a label and give it style
		      Label label = new Label("Label #" + i, false);
		      label.addStyleName("getting-started-label");

		      // add it to the DOM so that offset width/height becomes available
		      targetPanel.add(label, 0, 0);

		      // determine random label location within target panel
		      int left = Random.nextInt(DOMUtil.getClientWidth(targetPanel.getElement())
		          - label.getOffsetWidth());
		      int top = Random.nextInt(DOMUtil.getClientHeight(targetPanel.getElement())
		          - label.getOffsetHeight());

		      // move the label
		      targetPanel.setWidgetPosition(label, left, top);

		      // make the label draggable
		      dragController.makeDraggable(label);
		    }
	  }

}
