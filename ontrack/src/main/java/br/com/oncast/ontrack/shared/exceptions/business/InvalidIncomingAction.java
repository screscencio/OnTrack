package br.com.oncast.ontrack.shared.exceptions.business;


public class InvalidIncomingAction extends UnableToHandleActionException {

	private static final long serialVersionUID = 1L;

	public InvalidIncomingAction(final String message) {
		super(message);
	}
}
