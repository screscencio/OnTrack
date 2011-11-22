package br.com.oncast.ontrack.shared.exceptions.business;

public class ProjectNotFoundException extends BusinessException {

	private static final long serialVersionUID = 1L;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ProjectNotFoundException() {}

	public ProjectNotFoundException(final String errorMessage) {
		super(errorMessage);
	}
}
