package br.com.oncast.ontrack.shared.exceptions.authentication;

import java.io.Serializable;

public class InvalidAuthenticationCredentialsException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;

	public InvalidAuthenticationCredentialsException() {
		super();
	}

	public InvalidAuthenticationCredentialsException(final Exception e) {
		super(e);
	}

	public InvalidAuthenticationCredentialsException(final String message) {
		super(message);
	}

	public InvalidAuthenticationCredentialsException(final String message, final Exception e) {
		super(message, e);
	}
}