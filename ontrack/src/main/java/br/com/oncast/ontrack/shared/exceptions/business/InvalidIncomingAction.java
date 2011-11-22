package br.com.oncast.ontrack.shared.exceptions.business;

public class InvalidIncomingAction extends UnableToHandleActionException {

	private static final long serialVersionUID = 1L;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected InvalidIncomingAction() {}

	public InvalidIncomingAction(final String message) {
		super(message);
	}
}
