package br.com.oncast.ontrack.client.services.actionExecution;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;

import com.google.gwt.event.shared.HandlerRegistration;

public interface ActionExecutionService extends ActionExecutionRequestHandler {

	// TODO Analyze adding this method to ActionExecutionRequestHandler as 'onNonUserActionExecutionRequest'
	public void onNonUserActionRequest(final ModelAction action, ActionContext actionContext) throws UnableToCompleteActionException;

	public void onNonUserActionRequest(ModelAction action) throws UnableToCompleteActionException;

	@Override
	public void onUserActionExecutionRequest(final ModelAction action);

	@Override
	public void onUserActionUndoRequest();

	@Override
	public void onUserActionRedoRequest();

	public HandlerRegistration addActionExecutionListener(final ActionExecutionListener actionExecutionListener);

	public void removeActionExecutionListener(final ActionExecutionListener actionExecutionListener);

}