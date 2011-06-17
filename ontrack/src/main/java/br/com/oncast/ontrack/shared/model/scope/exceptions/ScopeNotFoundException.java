package br.com.oncast.ontrack.shared.model.scope.exceptions;

public class ScopeNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public ScopeNotFoundException(final String message) {
		super(message);
	}
}
