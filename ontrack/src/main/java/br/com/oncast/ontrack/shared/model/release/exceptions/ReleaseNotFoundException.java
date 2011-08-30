package br.com.oncast.ontrack.shared.model.release.exceptions;

public class ReleaseNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public ReleaseNotFoundException() {
		super();
	}

	public ReleaseNotFoundException(final String message) {
		super(message);
	}
}
