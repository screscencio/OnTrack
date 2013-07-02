package br.com.oncast.ontrack.shared.model.action.exceptions;

import br.com.oncast.ontrack.client.i18n.ActionExecutionErrorMessageTranslator;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.ModelAction;

import com.google.gwt.core.shared.GWT;

public class UnableToCompleteActionException extends Exception {

	private static final long serialVersionUID = 1L;
	private final ActionExecutionErrorMessageCode code;
	private final String[] errorMessageArgs;
	private final ModelAction action;

	public UnableToCompleteActionException(final ModelAction action, final ActionExecutionErrorMessageCode code, final String... errorMessageArgs) {
		super(code.name());
		this.action = action;
		this.code = code;
		this.errorMessageArgs = errorMessageArgs;
	}

	public UnableToCompleteActionException(final ModelAction action, final Throwable e) {
		super(e);
		this.action = action;
		this.code = ActionExecutionErrorMessageCode.UNKNOWN;
		this.errorMessageArgs = new String[] { e.getLocalizedMessage() };
	}

	@Override
	public String getLocalizedMessage() {
		if (GWT.isClient()) return ActionExecutionErrorMessageTranslator.translate(this.code, errorMessageArgs);

		return getClassSimpleName() + "[" + code.name() + ", " + errorMessageArgs + "]: " + action;
	}

	@Override
	public String toString() {
		final String s = getClassSimpleName();
		final String message = getMessage();
		return (message != null) ? (s + ": " + message) : s;
	}

	private String getClassSimpleName() {
		return getClass().getName().replaceAll(".*\\.", "");
	}
}
