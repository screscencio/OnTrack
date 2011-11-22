package br.com.oncast.ontrack.shared.exceptions.business;

public class UnableToHandleActionException extends BusinessException {

	private static final long serialVersionUID = 1L;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	public UnableToHandleActionException() {
		super();
	}

	public UnableToHandleActionException(final String message) {
		super(message);
	}
}
