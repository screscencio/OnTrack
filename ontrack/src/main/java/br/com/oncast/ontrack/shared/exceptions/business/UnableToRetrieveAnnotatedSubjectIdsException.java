package br.com.oncast.ontrack.shared.exceptions.business;

import java.io.Serializable;

public class UnableToRetrieveAnnotatedSubjectIdsException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;

	public UnableToRetrieveAnnotatedSubjectIdsException(final String message) {
		super(message);
	}

}
