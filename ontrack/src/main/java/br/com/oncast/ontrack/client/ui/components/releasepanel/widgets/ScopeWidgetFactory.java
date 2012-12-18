package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DropControllerFactory;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ScopeWidgetFactory implements ModelWidgetFactory<Scope, ReleaseScopeWidget> {

	private final DragAndDropManager scopeItemDragAndDropManager;
	private final boolean releaseSpecific;
	private final DragAndDropManager userDragAndDropMananger;
	private final DropControllerFactory userDropControllerFactory;

	public ScopeWidgetFactory(final DragAndDropManager scopeItemDragAndDropManager,
			final DragAndDropManager userDragAndDropMananger,
			final DropControllerFactory userDropControllerFactory,
			final boolean releaseSpecific) {

		this.scopeItemDragAndDropManager = scopeItemDragAndDropManager;
		this.userDragAndDropMananger = userDragAndDropMananger;
		this.userDropControllerFactory = userDropControllerFactory;
		this.releaseSpecific = releaseSpecific;
	}

	@Override
	public ReleaseScopeWidget createWidget(final Scope scope) {
		final ReleaseScopeWidget newScopeWidget = new ReleaseScopeWidget(scope, releaseSpecific, userDragAndDropMananger);

		scopeItemDragAndDropManager.monitorNewDraggableItem(newScopeWidget, newScopeWidget.getDraggableAnchor());
		if (userDragAndDropMananger != null && userDropControllerFactory != null) userDragAndDropMananger.monitorDropTarget(newScopeWidget,
				userDropControllerFactory);

		return newScopeWidget;
	}

}
