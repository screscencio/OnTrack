package br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories;

import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.CustomCommandMenuItemFactory;

public interface ScopeTreeItemWidgetCommandMenuItemFactory extends CustomCommandMenuItemFactory {

	CommandMenuItem createItem(String itemText, String valueToDeclare);
}
