package br.com.oncast.ontrack.shared.exceptions.authorization;

public class UnableToAuthorizeUserException extends Exception {

	private static final long serialVersionUID = 1L;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected UnableToAuthorizeUserException() {}

	public UnableToAuthorizeUserException(final String message) {
		super(message);
	}

	public UnableToAuthorizeUserException(final String message, final Throwable e) {
		super(message, e);
	}
}
