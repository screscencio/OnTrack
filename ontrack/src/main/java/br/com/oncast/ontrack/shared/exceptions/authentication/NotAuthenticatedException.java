package br.com.oncast.ontrack.shared.exceptions.authentication;

import com.google.gwt.user.client.rpc.IsSerializable;

public class NotAuthenticatedException extends Exception implements IsSerializable {

	private static final long serialVersionUID = 1L;

	public NotAuthenticatedException() {}
}
