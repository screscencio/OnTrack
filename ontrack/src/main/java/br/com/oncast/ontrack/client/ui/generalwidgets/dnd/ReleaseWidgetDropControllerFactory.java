package br.com.oncast.ontrack.client.ui.generalwidgets.dnd;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseWidgetDropControllerFactory implements DropControllerFactory {

	@Override
	public DropController create(final Widget panel) {
		if (!(panel instanceof VerticalPanel)) throw new RuntimeException("Impossible to create VerticalPanelDropController for class '" + panel.getClass()
				+ "'");
		return new VerticalPanelDropController((VerticalPanel) panel);
	}
}
