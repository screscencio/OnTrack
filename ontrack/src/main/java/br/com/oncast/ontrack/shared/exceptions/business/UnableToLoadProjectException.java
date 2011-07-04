package br.com.oncast.ontrack.shared.exceptions.business;


public class UnableToLoadProjectException extends BusinessException {

	private static final long serialVersionUID = 1L;

	public UnableToLoadProjectException() {
		super();
	}

	public UnableToLoadProjectException(final Exception e) {
		super(e);
	}

	public UnableToLoadProjectException(final String message) {
		super(message);
	}

	public UnableToLoadProjectException(final String message, final Exception e) {
		super(message, e);
	}
}
