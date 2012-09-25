package br.com.oncast.ontrack.shared.exceptions.business;

public class UnableToRetrieveNotificationListException extends Exception {

	private static final long serialVersionUID = 1L;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected UnableToRetrieveNotificationListException() {}

	public UnableToRetrieveNotificationListException(final String message) {
		super(message);
	}
}
