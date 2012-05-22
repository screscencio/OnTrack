package br.com.oncast.ontrack.server.services.exportImport.xml.exceptions;

public class UnableToExportXMLException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnableToExportXMLException(final String message, final Exception e) {
		super(message, e);
		e.printStackTrace();
	}
}
