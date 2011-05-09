package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

public class UnableToCompleteActionException extends Exception {
	private static final long serialVersionUID = 1L;

	public UnableToCompleteActionException(final String description) {
		super(description);
	}
}
