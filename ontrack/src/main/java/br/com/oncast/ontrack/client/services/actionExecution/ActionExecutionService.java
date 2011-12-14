package br.com.oncast.ontrack.client.services.actionExecution;

import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;

public interface ActionExecutionService extends ActionExecutionRequestHandler {

	// TODO Analyze adding this method to ActionExecutionRequestHandler as 'onNonUserActionExecutionRequest'
	public void onNonUserActionRequest(final ModelAction action) throws UnableToCompleteActionException;

	@Override
	public void onUserActionExecutionRequest(final ModelAction action);

	@Override
	public void onUserActionUndoRequest();

	@Override
	public void onUserActionRedoRequest();

	public void addActionExecutionListener(final ActionExecutionListener actionExecutionListener);

	public void removeActionExecutionListener(final ActionExecutionListener actionExecutionListener);

}