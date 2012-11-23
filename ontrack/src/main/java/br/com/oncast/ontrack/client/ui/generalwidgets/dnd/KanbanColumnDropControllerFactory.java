package br.com.oncast.ontrack.client.ui.generalwidgets.dnd;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class KanbanColumnDropControllerFactory implements DropControllerFactory {

	@Override
	public DropController create(final Widget panel) {
		if (!(panel instanceof HorizontalPanel)) throw new RuntimeException("Impossible to create HorizontalPanelDropController for class '" + panel.getClass()
				+ "'");
		return new KanbanColumnDropController((HorizontalPanel) panel);
	}

}
