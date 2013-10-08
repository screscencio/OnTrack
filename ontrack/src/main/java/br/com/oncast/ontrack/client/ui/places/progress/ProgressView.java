package br.com.oncast.ontrack.client.ui.places.progress;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.progresspanel.KanbanWidgetDisplay;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleasePanelWidget;
import br.com.oncast.ontrack.client.ui.places.progress.details.ProgressDetailPanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

public interface ProgressView extends IsWidget {

	ApplicationMenu getApplicationMenu();

	KanbanWidgetDisplay getKanbanPanel();

	FlowPanel getAlertingPanel();

	void registerActionExecutionHandler(ActionExecutionService actionExecutionService);

	void unregisterActionExecutionHandler(ActionExecutionService actionExecutionService);

	ProgressDetailPanel getProgressDetailWidget();

	ReleasePanelWidget getReleaseWidget();
}
