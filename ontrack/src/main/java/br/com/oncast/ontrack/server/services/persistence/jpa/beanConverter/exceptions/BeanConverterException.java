package br.com.oncast.ontrack.server.services.persistence.jpa.beanConverter.exceptions;

public class BeanConverterException extends Exception {

	private static final long serialVersionUID = 1L;

	public BeanConverterException(final String message) {
		super(message);
	}

	public BeanConverterException(final String message, final Throwable e) {
		super(message, e);
	}
}
