package br.com.oncast.ontrack.shared.model.action.exceptions;

import br.com.oncast.ontrack.client.i18n.ActionExecutionErrorMessageTranslator;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.utils.PrettyPrinter;

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

		return toString();
	}

	@Override
	public String toString() {
		final String causeString = super.getCause() == null ? "" : "\nCaused by " + PrettyPrinter.getSimpleName(super.getCause()) + ": " + super.getCause().getMessage();
		final String errorArgStr = errorMessageArgs == null ? "" : (", args=" + PrettyPrinter.getToStringListString((Object[]) errorMessageArgs));
		return getClassSimpleName() + "[" + code.name() + errorArgStr + "]: " + action + causeString;
	}

	private String getClassSimpleName() {
		return PrettyPrinter.getSimpleName(this);
	}

}
