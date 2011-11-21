package br.com.oncast.ontrack.server.services.exportImport.xml.exceptions;

import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;

public class UnableToExportXMLException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnableToExportXMLException(final String message, final PersistenceException e) {
		super(message, e);
	}

}
