package br.com.oncast.ontrack.shared.model.action.exceptions;

import br.com.oncast.ontrack.client.i18n.ActionExecutionErrorMessageTranslator;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;

public class UnableToCompleteActionException extends Exception {

	private static final long serialVersionUID = 1L;
	private final ActionExecutionErrorMessageCode code;
	private final String[] errorMessageArgs;

	public UnableToCompleteActionException(final ActionExecutionErrorMessageCode code, final String... errorMessageArgs) {
		super(code.name());
		this.code = code;
		this.errorMessageArgs = errorMessageArgs;
	}

	public UnableToCompleteActionException(final Throwable e) {
		super(e);
		this.code = ActionExecutionErrorMessageCode.UNKNOWN;
		this.errorMessageArgs = new String[] { e.getLocalizedMessage() };
	}

	@Override
	public String getLocalizedMessage() {
		return ActionExecutionErrorMessageTranslator.translate(this.code, errorMessageArgs);
	}
}
