package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd;

import br.com.oncast.ontrack.client.ui.components.ScopeWidget;
import br.com.oncast.ontrack.client.ui.components.members.DraggableMemberWidget;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;

public class ScopeWidgetDropController extends SimpleDropController implements DropController {

	private final ScopeWidget scopeWidget;
	private boolean wasSelected = false;

	public ScopeWidgetDropController(final ScopeWidget scopeWidget) {
		super(scopeWidget.asWidget());
		this.scopeWidget = scopeWidget;
	}

	@Override
	public void onEnter(final DragContext context) {
		super.onEnter(context);
		wasSelected = scopeWidget.isHighlighted();
		scopeWidget.setHighlighted(true);
	}

	@Override
	public void onLeave(final DragContext context) {
		super.onLeave(context);
		scopeWidget.setHighlighted(wasSelected);
	}

	@Override
	public void onDrop(final DragContext context) {
		super.onDrop(context);
		scopeWidget.addAssociatedUsers((DraggableMemberWidget) context.draggable);
	}

}
