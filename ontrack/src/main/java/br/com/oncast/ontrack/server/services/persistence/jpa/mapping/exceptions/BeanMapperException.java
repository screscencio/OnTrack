package br.com.oncast.ontrack.server.services.persistence.jpa.mapping.exceptions;

public class BeanMapperException extends Exception {

	private static final long serialVersionUID = 1L;

	public BeanMapperException(final String message) {
		super(message);
	}

	public BeanMapperException(final String message, final Throwable e) {
		super(message, e);
	}
}
