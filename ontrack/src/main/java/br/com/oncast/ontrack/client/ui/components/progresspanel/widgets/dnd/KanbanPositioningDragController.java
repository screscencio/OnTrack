package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.dnd;

import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.ScopeWidget;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

final class KanbanPositioningDragController extends VerticalPanelDropController {
	private Widget refPositioner;
	private boolean isPriorityChange;
	private int dropIndex;
	private final KanbanColumn kanbanColumn;
	private final Release release;

	KanbanPositioningDragController(final VerticalPanel dropTarget, final Release release, final KanbanColumn kanbanColumn) {
		super(dropTarget);
		this.release = release;
		this.kanbanColumn = kanbanColumn;
	}

	@Override
	public void onEnter(final DragContext context) {
		super.onEnter(context);
		final ScopeWidget draggedScope = (ScopeWidget) context.draggable;
		isPriorityChange = isPriorityChange(draggedScope.getModelObject(), kanbanColumn.getDescription());
		if (!isPriorityChange) {
			dropIndex = findIndex(draggedScope);
		}
	}

	@Override
	public void onLeave(final DragContext context) {
		super.onLeave(context);
		refPositioner = null;
	}

	@Override
	public void onMove(final DragContext context) {
		super.onMove(context);
		if (isPriorityChange) return;
		((VerticalPanel) dropTarget).remove(refPositioner);
		dropTarget.insert(refPositioner, dropIndex);
	}

	private int findIndex(final ScopeWidget draggedScope) {
		int index = 0;
		final int draggedPriority = release.getScopeIndex(draggedScope.getModelObject());
		for (final Widget widget : (VerticalPanel) dropTarget) {
			if (!(widget instanceof ScopeWidget)) continue;
			final ScopeWidget w = (ScopeWidget) widget;
			if (release.getScopeIndex(w.getModelObject()) > draggedPriority) return index;
			index++;
		}
		return index;
	}

	@Override
	protected Widget newPositioner(final DragContext context) {
		return refPositioner = super.newPositioner(context);
	}

	private boolean isPriorityChange(final Scope scope, final String title) {
		if (scope.getProgress().getState() == ProgressState.NOT_STARTED && ProgressState.getStateForDescription(title) == ProgressState.NOT_STARTED) return true;
		return scope.getProgress().getDescription().equals(title);
	}
}