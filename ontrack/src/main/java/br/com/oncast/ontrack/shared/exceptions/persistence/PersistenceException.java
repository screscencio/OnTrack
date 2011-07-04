package br.com.oncast.ontrack.shared.exceptions.persistence;

public class PersistenceException extends Exception {

	private static final long serialVersionUID = 1L;

	public PersistenceException() {
		super();
	}

	public PersistenceException(final String message, final Exception e) {
		super(message, e);
	}
}
