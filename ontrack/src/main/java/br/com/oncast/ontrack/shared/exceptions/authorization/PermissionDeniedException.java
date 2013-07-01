package br.com.oncast.ontrack.shared.exceptions.authorization;

public class PermissionDeniedException extends AuthorizationException {

	private static final long serialVersionUID = 1L;

	public PermissionDeniedException() {
		super();
	}

	public PermissionDeniedException(final String message) {
		super(message);
	}
}
