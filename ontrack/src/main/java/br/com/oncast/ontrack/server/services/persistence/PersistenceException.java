package br.com.oncast.ontrack.server.services.persistence;

public class PersistenceException extends Exception {

	private static final long serialVersionUID = 1L;

	public PersistenceException(final String message, final Exception e) {
		super(message, e);
	}
}
