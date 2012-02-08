package br.com.oncast.ontrack.client.ui.generalwidgets.dnd;

import com.allen_sauer.gwt.dnd.client.drop.AbstractInsertPanelDropController;
import com.allen_sauer.gwt.dnd.client.drop.HorizontalPanelDropController;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DropControllerFactory {

	public static AbstractInsertPanelDropController create(final CellPanel panel) {
		if (panel instanceof VerticalPanel) return new VerticalPanelDropController((VerticalPanel) panel);
		if (panel instanceof HorizontalPanel) return new HorizontalPanelDropController((HorizontalPanel) panel);
		throw new RuntimeException("There is no mapped controller for the class '" + panel.getClass() + "'");
	}

}
