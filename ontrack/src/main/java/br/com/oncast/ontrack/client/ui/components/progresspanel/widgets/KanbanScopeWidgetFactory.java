package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets;

import br.com.oncast.ontrack.client.ui.components.progresspanel.interaction.ProgressPanelWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public final class KanbanScopeWidgetFactory implements ModelWidgetFactory<Scope, ScopeWidget> {

	private final ProgressPanelWidgetInteractionHandler handler;
	private final DragAndDropManager dragAndDropManager;

	public KanbanScopeWidgetFactory(final DragAndDropManager dragAndDropManager, final ProgressPanelWidgetInteractionHandler handler) {
		this.dragAndDropManager = dragAndDropManager;
		this.handler = handler;
	}

	@Override
	public ScopeWidget createWidget(final Scope modelBean) {
		final ScopeWidget scopeWidget = new ScopeWidget(modelBean, handler);
		dragAndDropManager.monitorNewDraggableItem(scopeWidget, scopeWidget.getDraggableAnchor());
		return scopeWidget;
	}
}