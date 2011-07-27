package br.com.oncast.ontrack.server.util.typeConverter.exceptions;

public class TypeConverterException extends Exception {

	private static final long serialVersionUID = 1L;

	public TypeConverterException(final String message) {
		super(message);
	}

	public TypeConverterException(final String message, final Throwable e) {
		super(message, e);
	}
}
