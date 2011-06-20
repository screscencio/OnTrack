package br.com.oncast.ontrack.client.ui.components;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;

import com.google.gwt.user.client.ui.IsWidget;

public interface Component extends IsWidget {

	public abstract ActionExecutionListener getActionExecutionListener();

	public abstract void setActionExecutionRequestHandler(final ActionExecutionRequestHandler actionHandler);

}