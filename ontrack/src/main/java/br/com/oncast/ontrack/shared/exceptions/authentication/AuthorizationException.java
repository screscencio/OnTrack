package br.com.oncast.ontrack.shared.exceptions.authentication;

// FIXME Rodrigo: Threat this exception in the client.
public class AuthorizationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AuthorizationException() {
		super();
	}

	public AuthorizationException(final String message) {
		super(message);
	}
}
