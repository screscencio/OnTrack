package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ScopeWidgetFactory implements ModelWidgetFactory<Scope, ScopeWidget> {

	private final DragAndDropManager dragAndDropManager;
	private final boolean kanbanSpecific;

	public ScopeWidgetFactory(final DragAndDropManager dragAndDropManager) {
		this(dragAndDropManager, false);
	}

	public ScopeWidgetFactory(final DragAndDropManager dragAndDropManager, final boolean kanbanSpecific) {
		this.dragAndDropManager = dragAndDropManager;
		this.kanbanSpecific = kanbanSpecific;
	}

	@Override
	public ScopeWidget createWidget(final Scope scope) {
		final ScopeWidget newScopeWidget = new ScopeWidget(scope, kanbanSpecific);

		dragAndDropManager.monitorNewDraggableItem(newScopeWidget, newScopeWidget.getDraggableAnchor());

		return newScopeWidget;
	}
}
