package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ScopeWidgetFactory implements ModelWidgetFactory<Scope, ScopeWidget> {

	private final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler;
	private final DragAndDropManager dragAndDropManager;

	public ScopeWidgetFactory(final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler,
			final DragAndDropManager dragAndDropManager) {

		this.releasePanelInteractionHandler = releasePanelInteractionHandler;
		this.dragAndDropManager = dragAndDropManager;
	}

	@Override
	public ScopeWidget createWidget(final Scope scope) {
		final ScopeWidget newScopeWidget = new ScopeWidget(scope, releasePanelInteractionHandler);

		dragAndDropManager.monitorNewDraggableItem(newScopeWidget, newScopeWidget.getDraggableAnchor());

		return newScopeWidget;
	}
}
