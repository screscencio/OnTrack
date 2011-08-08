package br.com.oncast.ontrack.shared.exceptions.business;

import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class InvalidIncomingAction extends UnableToHandleActionException {

	private static final long serialVersionUID = 1L;

	public InvalidIncomingAction(final UnableToCompleteActionException e) {
		super(e);
	}
}
