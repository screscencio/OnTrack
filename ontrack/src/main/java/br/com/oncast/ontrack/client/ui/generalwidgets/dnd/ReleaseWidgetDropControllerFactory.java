package br.com.oncast.ontrack.client.ui.generalwidgets.dnd;


import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ReleaseWidgetDropControllerFactory implements DropControllerFactory {

	@Override
	public DropController create(final CellPanel panel) {
		if (!(panel instanceof VerticalPanel)) throw new RuntimeException("Impossible to create VerticalPanelDropController for class '" + panel.getClass()
				+ "'");
		return new VerticalPanelDropController((VerticalPanel) panel);
	}
}
