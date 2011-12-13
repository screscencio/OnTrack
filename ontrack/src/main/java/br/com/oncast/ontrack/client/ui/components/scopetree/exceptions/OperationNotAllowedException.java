package br.com.oncast.ontrack.client.ui.components.scopetree.exceptions;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;

public class OperationNotAllowedException extends UnableToCompleteActionException {

	private static final long serialVersionUID = 1L;

	public OperationNotAllowedException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public OperationNotAllowedException(final String message) {
		super(message);
	}

	public OperationNotAllowedException(final Throwable cause) {
		super(cause);
	}

}
