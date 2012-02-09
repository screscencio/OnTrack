package br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.dnd;

import br.com.oncast.ontrack.client.ui.components.progresspanel.widgets.KanbanColumnWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DropControllerFactory;
import br.com.oncast.ontrack.shared.model.release.Release;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class KanbanPositioningDropControllerFactory implements DropControllerFactory {

	private final KanbanColumnWidget kanbanColumn;
	private final Release release;

	public KanbanPositioningDropControllerFactory(final KanbanColumnWidget kanbanColumnWidget, final Release release) {
		this.kanbanColumn = kanbanColumnWidget;
		this.release = release;
	}

	@Override
	public DropController create(final CellPanel panel) {
		if (!(panel instanceof VerticalPanel)) throw new RuntimeException("Impossible to create LOLVerticalPanelDropController for class '" + panel.getClass()
				+ "'");
		return new KanbanPositioningDragController((VerticalPanel) panel, release, kanbanColumn);
	}

}
