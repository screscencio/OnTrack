package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets;

import br.com.oncast.ontrack.client.ui.components.scope.ScopeCardWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DropControllerFactory;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public final class KanbanScopeWidgetFactory implements ModelWidgetFactory<Scope, ScopeCardWidget> {

	private final DragAndDropManager dragAndDropManager;
	private final DragAndDropManager userDragAndDropManager;
	private final DropControllerFactory userDropControllerFactory;

	public KanbanScopeWidgetFactory(final DragAndDropManager dragAndDropManager,
			final DragAndDropManager userDragAndDropManager, final DropControllerFactory userDropControllerFactory) {
		this.dragAndDropManager = dragAndDropManager;
		this.userDragAndDropManager = userDragAndDropManager;
		this.userDropControllerFactory = userDropControllerFactory;
	}

	@Override
	public ScopeCardWidget createWidget(final Scope modelBean) {
		final ScopeCardWidget scopeWidget = new ScopeCardWidget(modelBean, true, userDragAndDropManager);
		dragAndDropManager.monitorNewDraggableItem(scopeWidget, scopeWidget.getDraggableAnchor());
		userDragAndDropManager.monitorDropTarget(scopeWidget, userDropControllerFactory);
		return scopeWidget;
	}
}