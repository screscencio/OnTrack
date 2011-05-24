package br.com.oncast.ontrack.shared.scope.exceptions;


public class UnableToCompleteActionException extends Exception {
	private static final long serialVersionUID = 1L;

	public UnableToCompleteActionException(final String description) {
		super(description);
	}

	public UnableToCompleteActionException(final String description, final Throwable e) {
		super(description, e);
	}
}
