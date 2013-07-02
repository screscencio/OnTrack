package br.com.oncast.ontrack.client.ui.components.scopetree.exceptions;

import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;

public class OperationNotAllowedException extends UnableToCompleteActionException {

	private static final long serialVersionUID = 1L;

	public OperationNotAllowedException(final ModelAction action, final ActionExecutionErrorMessageCode code, final String... errorMessageArgs) {
		super(action, code, errorMessageArgs);
	}

	public OperationNotAllowedException(final ModelAction action, final Throwable cause) {
		super(action, cause);
	}

}
