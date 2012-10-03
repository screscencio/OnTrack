package br.com.oncast.ontrack.shared.exceptions.business;

public class UnableToPostProcessActionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected UnableToPostProcessActionException() {}

	public UnableToPostProcessActionException(final String message) {
		super(message);
	}

	public UnableToPostProcessActionException(final String message, final Exception e) {
		super(message, e);
	}

}