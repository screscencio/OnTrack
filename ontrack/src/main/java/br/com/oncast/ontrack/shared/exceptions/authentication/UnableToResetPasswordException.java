package br.com.oncast.ontrack.shared.exceptions.authentication;

public class UnableToResetPasswordException extends Exception {

	private static final long serialVersionUID = 1L;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected UnableToResetPasswordException() {}

	public UnableToResetPasswordException(final String message) {
		super(message);
	}

	public UnableToResetPasswordException(final String message, final Throwable e) {
		super(message, e);
	}

}
