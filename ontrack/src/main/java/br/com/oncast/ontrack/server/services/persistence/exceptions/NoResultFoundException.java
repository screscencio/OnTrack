package br.com.oncast.ontrack.server.services.persistence.exceptions;

import javax.persistence.NoResultException;

public class NoResultFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoResultFoundException(final String message, final NoResultException e) {
		super(message, e);
	}

}
