package br.com.oncast.ontrack.shared.exceptions.authorization;

// TODO +++++Threat this exception in the client.
public class AuthorizationException extends Exception {
	private static final long serialVersionUID = 1L;

	public AuthorizationException() {
		super();
	}

	public AuthorizationException(final String message) {
		super(message);
	}
}
