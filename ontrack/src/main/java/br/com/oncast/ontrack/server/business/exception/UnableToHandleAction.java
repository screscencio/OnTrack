package br.com.oncast.ontrack.server.business.exception;

import br.com.oncast.ontrack.server.services.persistence.PersistenceException;

public class UnableToHandleAction extends BusinessException {

	private static final long serialVersionUID = 1L;

	public UnableToHandleAction(final PersistenceException e) {
		super(e);
	}

}
