package br.com.oncast.ontrack.client.services.actionExecution;

import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.UserAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;

import com.google.gwt.event.shared.HandlerRegistration;

public interface ActionExecutionService extends ActionExecutionRequestHandler {

	void onNonUserActionRequest(UserAction action) throws UnableToCompleteActionException;

	@Override
	void onUserActionExecutionRequest(final ModelAction action);

	@Override
	public void onUserActionUndoRequest();

	@Override
	public void onUserActionRedoRequest();

	public HandlerRegistration addActionExecutionListener(final ActionExecutionListener actionExecutionListener);

	public void removeActionExecutionListener(final ActionExecutionListener actionExecutionListener);

}