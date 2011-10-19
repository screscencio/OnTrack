package br.com.oncast.ontrack.shared.exceptions.authentication;

import java.io.Serializable;

public class UserNotFoundException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;

	public UserNotFoundException() {
		super();
	}

	public UserNotFoundException(final Exception e) {
		super(e);
	}

	public UserNotFoundException(final String message) {
		super(message);
	}

	public UserNotFoundException(final String message, final Exception e) {
		super(message, e);
	}
}
