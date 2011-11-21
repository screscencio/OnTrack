package br.com.oncast.ontrack.server.services.exportImport.xml.exceptions;


public class UnableToImportXMLException extends RuntimeException {

	public UnableToImportXMLException(final String message, final Exception e) {
		super(message, e);
	}

	private static final long serialVersionUID = 1L;

}
