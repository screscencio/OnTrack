package br.com.oncast.ontrack.shared.model.action.exceptions.copy;

public class UnableToCompleteActionException extends Exception {
	private static final long serialVersionUID = 1L;

	public UnableToCompleteActionException(final String description) {
		super(description);
	}

	public UnableToCompleteActionException(final String description, final Throwable e) {
		super(description, e);
	}

	public UnableToCompleteActionException(final Throwable e) {
		super(e);
	}
}
