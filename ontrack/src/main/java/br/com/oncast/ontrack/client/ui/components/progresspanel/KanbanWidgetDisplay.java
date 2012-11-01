package br.com.oncast.ontrack.client.ui.components.progresspanel;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;

public interface KanbanWidgetDisplay {

	public void setActionExecutionService(ActionExecutionService actionExecutionService);

	public void update();
}