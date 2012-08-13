package br.com.oncast.ontrack.shared.exceptions.business;

import java.io.Serializable;

public class UnableToRetrieveAnnotationsListException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;

	public UnableToRetrieveAnnotationsListException(final String message) {
		super(message);
	}

}
