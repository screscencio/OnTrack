package br.com.oncast.ontrack.shared.exceptions.business;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class BusinessException extends Exception implements IsSerializable {

	private static final long serialVersionUID = 1L;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	public BusinessException() {
		super();
	}

	public BusinessException(final String message) {
		super(message);
	}
}
