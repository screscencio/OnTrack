package br.com.oncast.ontrack.client.ui.generalwidgets.dnd;

import br.com.oncast.ontrack.client.services.globalEvent.GlobalNativeEventService;
import br.com.oncast.ontrack.client.services.globalEvent.NativeEventListener;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.HorizontalPanelDropController;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class KanbanColumnDropController extends HorizontalPanelDropController implements DropController {

	private Widget refPositioner = null;
	private NativeEventListener mouseUpListener = null;
	private boolean leaved = true;
	private int originalIndex = -1;

	public KanbanColumnDropController(final HorizontalPanel dropTarget) {
		super(dropTarget);
	}

	@Override
	public void onLeave(final DragContext context) {
		configureCleanHandler(context);
		this.leaved = true;
		movePositionerToOriginalIndex();
	}

	@Override
	public void onEnter(final DragContext context) {
		clearPositioner(context);

		super.onEnter(context);
		saveOriginalIndex();
		this.leaved = false;
	}

	private void saveOriginalIndex() {
		if (originalIndex == -1) originalIndex = dropTarget.getWidgetIndex(refPositioner);
	}

	private void configureCleanHandler(final DragContext context) {
		if (mouseUpListener != null) return;

		mouseUpListener = new NativeEventListener() {
			@Override
			public void onNativeEvent(final NativeEvent nativeEvent) {
				GlobalNativeEventService.getInstance().removeMouseUpListener(mouseUpListener);
				mouseUpListener = null;
				if (leaved) clearPositioner(context);
			}
		};
		GlobalNativeEventService.getInstance().addMouseUpListener(mouseUpListener);
	}

	private void movePositionerToOriginalIndex() {
		refPositioner.removeFromParent();
		dropTarget.insert(refPositioner, originalIndex);
	}

	private void clearPositioner(final DragContext context) {
		if (refPositioner == null) return;

		super.onLeave(context);
		refPositioner = null;
	}

	@Override
	protected Widget newPositioner(final DragContext context) {
		refPositioner = super.newPositioner(context);
		refPositioner.addStyleName("kanbanColumn-draggable-positioner");
		return refPositioner;
	}
}
