package br.com.oncast.ontrack.client.ui.places.progress;

import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.progresspanel.KanbanWigetDisplay;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface ProgressView extends IsWidget {

	ApplicationMenu getApplicationMenu();

	KanbanWigetDisplay getKanbanPanel();

	Widget getNotificationPanel();
}
