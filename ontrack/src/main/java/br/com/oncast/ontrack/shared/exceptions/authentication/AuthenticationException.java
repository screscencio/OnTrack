package br.com.oncast.ontrack.shared.exceptions.authentication;

import java.io.Serializable;

public class AuthenticationException extends RuntimeException implements Serializable {

	private static final long serialVersionUID = 1L;

	public AuthenticationException() {
		super();
	}

	public AuthenticationException(final Exception e) {
		super(e);
	}

	public AuthenticationException(final String message) {
		super(message);
	}

	public AuthenticationException(final String message, final Exception e) {
		super(message, e);
	}
}