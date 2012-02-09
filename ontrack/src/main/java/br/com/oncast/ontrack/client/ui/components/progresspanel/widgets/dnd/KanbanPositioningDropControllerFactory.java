package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.dnd;

import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DropControllerFactory;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.release.Release;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class KanbanPositioningDropControllerFactory implements DropControllerFactory {

	private final KanbanColumn kanbanColumn;
	private final Release release;

	public KanbanPositioningDropControllerFactory(final KanbanColumn kanbanColumn, final Release release) {
		this.kanbanColumn = kanbanColumn;
		this.release = release;
	}

	@Override
	public DropController create(final CellPanel panel) {
		if (!(panel instanceof VerticalPanel)) throw new RuntimeException("Impossible to create LOLVerticalPanelDropController for class '" + panel.getClass()
				+ "'");
		return new KanbanPositioningDragController((VerticalPanel) panel, release, kanbanColumn);
	}

}
