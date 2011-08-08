package br.com.oncast.ontrack.shared.exceptions.business;

public class UnableToHandleActionException extends BusinessException {

	private static final long serialVersionUID = 1L;

	public UnableToHandleActionException() {
		super();
	}

	public UnableToHandleActionException(final String message, final Exception e) {
		super(message, e);
	}

	public UnableToHandleActionException(final Exception e) {
		super(e);
	}
}
