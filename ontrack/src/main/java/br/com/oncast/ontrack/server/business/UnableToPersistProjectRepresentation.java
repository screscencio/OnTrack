package br.com.oncast.ontrack.server.business;

import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.BusinessException;

public class UnableToPersistProjectRepresentation extends BusinessException {

	private static final long serialVersionUID = 1L;

	public UnableToPersistProjectRepresentation(final PersistenceException e) {
		super(e);
	}

}
