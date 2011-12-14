package br.com.oncast.ontrack.shared.exceptions.authentication;

import java.io.Serializable;

public class UserNotFoundException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;

	public UserNotFoundException() {
		super();
	}

	public UserNotFoundException(final String message) {
		super(message);
	}
}
