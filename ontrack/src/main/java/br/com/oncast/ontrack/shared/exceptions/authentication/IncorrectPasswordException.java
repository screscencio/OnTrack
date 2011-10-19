package br.com.oncast.ontrack.shared.exceptions.authentication;

import java.io.Serializable;

public class IncorrectPasswordException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;

	public IncorrectPasswordException() {
		super();
	}

	public IncorrectPasswordException(final Exception e) {
		super(e);
	}

	public IncorrectPasswordException(final String message) {
		super(message);
	}

	public IncorrectPasswordException(final String message, final Exception e) {
		super(message, e);
	}
}