package br.com.oncast.ontrack.server.services.persistence.exceptions;

public class PersistenceException extends Exception {

	private static final long serialVersionUID = 1L;

	public PersistenceException() {
		super();
	}

	public PersistenceException(final String message, final Exception e) {
		super(message, e);
	}

	public PersistenceException(final Exception e) {
		super(e);
	}
}
