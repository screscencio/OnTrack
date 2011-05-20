package br.com.oncast.ontrack.client.ui.component.scopetree.exceptions;

public class UnableToCompleteActionException extends Exception {
	private static final long serialVersionUID = 1L;

	public UnableToCompleteActionException(final String description) {
		super(description);
	}

	public UnableToCompleteActionException(final String description, final NotFoundException e) {
		super(description, e);
	}
}
