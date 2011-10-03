package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ScopeWidgetFactory implements ModelWidgetFactory<Scope, ScopeWidget> {

	private final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler;
	private final DraggableItemCreationListener draggableItemCreationListener;

	public ScopeWidgetFactory(final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler,
			final DraggableItemCreationListener draggableItemCreationListener) {

		this.releasePanelInteractionHandler = releasePanelInteractionHandler;
		this.draggableItemCreationListener = draggableItemCreationListener;
	}

	@Override
	public ScopeWidget createWidget(final Scope scope) {
		final ScopeWidget newScopeWidget = new ScopeWidget(scope, releasePanelInteractionHandler);
		draggableItemCreationListener.onDraggableItemCreated(newScopeWidget, newScopeWidget.getDraggableArea());

		return newScopeWidget;
	}
}
