package br.com.oncast.ontrack.shared.exceptions.business;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class BusinessException extends Exception implements IsSerializable {

	private static final long serialVersionUID = 1L;

	public BusinessException() {
		super();
	}

	public BusinessException(final Exception e) {
		super(e);
	}

	public BusinessException(final String message) {
		super(message);
	}

	public BusinessException(final String message, final Exception e) {
		super(message, e);
	}
}
