package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd;

import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ScopeWidget;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;

public class ScopeWidgetDropController extends SimpleDropController implements DropController {

	private final ScopeWidget scopeWidget;

	public ScopeWidgetDropController(final ScopeWidget scopeWidget) {
		super(scopeWidget);
		this.scopeWidget = scopeWidget;
	}

	@Override
	public void onEnter(final DragContext context) {
		super.onEnter(context);
		scopeWidget.setSelected(true);
	}

	@Override
	public void onLeave(final DragContext context) {
		super.onLeave(context);
		scopeWidget.setSelected(false);
	}

	@Override
	public void onDrop(final DragContext context) {
		super.onDrop(context);
		scopeWidget.getAssociatedUsersContainer().add(context.draggable);
	}

}
