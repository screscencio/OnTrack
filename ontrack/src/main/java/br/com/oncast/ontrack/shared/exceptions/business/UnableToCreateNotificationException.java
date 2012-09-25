package br.com.oncast.ontrack.shared.exceptions.business;

public class UnableToCreateNotificationException extends BusinessException {

	private static final long serialVersionUID = 1L;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	public UnableToCreateNotificationException() {
		super();
	}

	public UnableToCreateNotificationException(final String message) {
		super(message);
	}

}
