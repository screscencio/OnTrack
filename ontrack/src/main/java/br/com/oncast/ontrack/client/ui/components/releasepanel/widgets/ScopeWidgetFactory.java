package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ScopeWidgetFactory implements ModelWidgetFactory<Scope, ScopeWidget> {

	private final DragAndDropManager dragAndDropManager;
	private final boolean shouldShowScopeColor;

	public ScopeWidgetFactory(final DragAndDropManager dragAndDropManager, final boolean shouldShowScopeColor) {
		this.dragAndDropManager = dragAndDropManager;
		this.shouldShowScopeColor = shouldShowScopeColor;
	}

	@Override
	public ScopeWidget createWidget(final Scope scope) {
		final ScopeWidget newScopeWidget = new ScopeWidget(scope, shouldShowScopeColor);

		dragAndDropManager.monitorNewDraggableItem(newScopeWidget, newScopeWidget.getDraggableAnchor());

		return newScopeWidget;
	}
}
