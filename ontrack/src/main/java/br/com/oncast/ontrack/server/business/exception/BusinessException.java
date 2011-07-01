package br.com.oncast.ontrack.server.business.exception;

import br.com.oncast.ontrack.server.services.persistence.PersistenceException;

public class BusinessException extends Exception {

	private static final long serialVersionUID = 1L;

	public BusinessException(final PersistenceException e) {
		super(e);
	}

	public BusinessException(final String message, final Exception e) {
		super(message, e);
	}

}
