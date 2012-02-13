package br.com.oncast.ontrack.client.ui.generalwidgets.dnd;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class KanbanColumnDropControllerFactory implements DropControllerFactory {

	@Override
	public DropController create(final CellPanel panel) {
		if (!(panel instanceof HorizontalPanel)) throw new RuntimeException("Impossible to create HorizontalPanelDropController for class '" + panel.getClass()
				+ "'");
		return new KanbanColumnDropController((HorizontalPanel) panel);
	}

}
