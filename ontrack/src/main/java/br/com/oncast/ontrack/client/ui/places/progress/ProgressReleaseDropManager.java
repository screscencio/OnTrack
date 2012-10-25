package br.com.oncast.ontrack.client.ui.places.progress;

import br.com.oncast.ontrack.client.ui.components.releasepanel.interaction.ReleasePanelInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ScopeWidget;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.Widget;

class ProgressReleaseDropManager implements DropController {

	private final Widget target;
	private final ReleasePanelInteractionHandler interactionHandler;
	private final String selectedStyleClass;

	public ProgressReleaseDropManager(final Widget target, final ReleasePanelInteractionHandler interactionHandler, final String selectedStyleClass) {
		this.target = target;
		this.interactionHandler = interactionHandler;
		this.selectedStyleClass = selectedStyleClass;
	}

	@Override
	public Widget getDropTarget() {
		return target;
	}

	@Override
	public void onDrop(final DragContext context) {
		interactionHandler.onScopeUnderworkdDropRequest(((ScopeWidget) context.draggable).getScope());
	}

	@Override
	public void onEnter(final DragContext context) {
		target.addStyleName(selectedStyleClass);
	}

	@Override
	public void onLeave(final DragContext context) {
		target.removeStyleName(selectedStyleClass);
	}

	@Override
	public void onMove(final DragContext context) {}

	@Override
	public void onPreviewDrop(final DragContext context) throws VetoDragException {}
}