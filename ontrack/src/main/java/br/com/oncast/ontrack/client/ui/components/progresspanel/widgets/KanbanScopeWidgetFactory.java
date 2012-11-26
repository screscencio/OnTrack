package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets;

import br.com.oncast.ontrack.client.ui.components.progresspanel.interaction.ProgressPanelWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DropControllerFactory;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public final class KanbanScopeWidgetFactory implements ModelWidgetFactory<Scope, KanbanScopeWidget> {

	private final ProgressPanelWidgetInteractionHandler handler;
	private final DragAndDropManager dragAndDropManager;
	private final DragAndDropManager userDragAndDropManager;
	private final DropControllerFactory userDropControllerFactory;

	public KanbanScopeWidgetFactory(final DragAndDropManager dragAndDropManager, final ProgressPanelWidgetInteractionHandler handler,
			final DragAndDropManager userDragAndDropManager, final DropControllerFactory userDropControllerFactory) {
		this.dragAndDropManager = dragAndDropManager;
		this.handler = handler;
		this.userDragAndDropManager = userDragAndDropManager;
		this.userDropControllerFactory = userDropControllerFactory;
	}

	@Override
	public KanbanScopeWidget createWidget(final Scope modelBean) {
		final KanbanScopeWidget scopeWidget = new KanbanScopeWidget(modelBean, handler, userDragAndDropManager);
		dragAndDropManager.monitorNewDraggableItem(scopeWidget, scopeWidget.getDraggableAnchor());
		userDragAndDropManager.monitorDropTarget(scopeWidget, userDropControllerFactory);
		return scopeWidget;
	}
}