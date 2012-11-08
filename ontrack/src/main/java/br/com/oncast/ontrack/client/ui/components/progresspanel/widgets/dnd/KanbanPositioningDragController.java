package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.dnd;

import java.util.List;

import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.KanbanColumnWidget;
import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.ScopeWidget;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

final class KanbanPositioningDragController extends VerticalPanelDropController {
	private Widget refPositioner;
	private int dropIndex;
	private final KanbanColumnWidget kanbanColumnWidget;
	private final Release release;

	KanbanPositioningDragController(final VerticalPanel dropTarget, final Release release, final KanbanColumnWidget kanbanColumnWidget) {
		super(dropTarget);
		this.release = release;
		this.kanbanColumnWidget = kanbanColumnWidget;
	}

	@Override
	public void onEnter(final DragContext context) {
		super.onEnter(context);
		final ScopeWidget draggedScope = (ScopeWidget) context.draggable;
		dropIndex = findIndex(draggedScope);
	}

	@Override
	public void onLeave(final DragContext context) {
		super.onLeave(context);
		refPositioner = null;
		kanbanColumnWidget.setHighlight(false);
	}

	@Override
	public void onMove(final DragContext context) {
		super.onMove(context);
		((VerticalPanel) dropTarget).remove(refPositioner);
		dropTarget.insert(refPositioner, dropIndex);
		kanbanColumnWidget.setHighlight(true);
	}

	private int findIndex(final ScopeWidget draggedScope) {
		int index = 0;
		final List<Scope> tasks = release.getTasks();
		final int draggedPriority = tasks.indexOf(draggedScope.getModelObject());
		for (final Widget widget : (VerticalPanel) dropTarget) {
			if (!(widget instanceof ScopeWidget)) continue;

			final ScopeWidget w = (ScopeWidget) widget;
			if (tasks.indexOf(w.getModelObject()) >= draggedPriority) return index;
			index++;
		}
		return index;
	}

	@Override
	protected Widget newPositioner(final DragContext context) {
		return refPositioner = super.newPositioner(context);
	}

}