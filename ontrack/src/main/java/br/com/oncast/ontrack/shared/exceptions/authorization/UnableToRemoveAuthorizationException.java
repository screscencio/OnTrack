package br.com.oncast.ontrack.shared.exceptions.authorization;

public class UnableToRemoveAuthorizationException extends Exception {

	private static final long serialVersionUID = 1L;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected UnableToRemoveAuthorizationException() {}

	public UnableToRemoveAuthorizationException(final String message, final Throwable e) {
		super(message, e);
	}

	public UnableToRemoveAuthorizationException(final String message) {
		super(message);
	}
}
