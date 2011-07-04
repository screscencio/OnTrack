package br.com.oncast.ontrack.shared.exceptions.converter;

public class BeanConverterException extends Exception {

	private static final long serialVersionUID = 1L;

	public BeanConverterException(final String message) {
		super(message);
	}

	public BeanConverterException(final String message, final Throwable e) {
		super(message, e);
	}
}
