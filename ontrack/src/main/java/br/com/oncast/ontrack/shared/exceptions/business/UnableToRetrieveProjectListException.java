package br.com.oncast.ontrack.shared.exceptions.business;

public class UnableToRetrieveProjectListException extends Exception {

	private static final long serialVersionUID = 1L;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected UnableToRetrieveProjectListException() {}

	public UnableToRetrieveProjectListException(final String message) {
		super(message);
	}
}
