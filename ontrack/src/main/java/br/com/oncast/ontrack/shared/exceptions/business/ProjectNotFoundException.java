package br.com.oncast.ontrack.shared.exceptions.business;

public class ProjectNotFoundException extends UnableToLoadProjectException {

	private static final long serialVersionUID = -4466451770388653802L;

	public ProjectNotFoundException(final String errorMessage, final Exception e) {
		super(errorMessage, e);
	}

}
