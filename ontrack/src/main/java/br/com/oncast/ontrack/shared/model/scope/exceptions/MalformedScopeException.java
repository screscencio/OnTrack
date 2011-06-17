package br.com.oncast.ontrack.shared.model.scope.exceptions;

public class MalformedScopeException extends RuntimeException {

	private static final long serialVersionUID = -7083164649210647907L;

	public MalformedScopeException(final String message) {
		super(message);
	}
}
