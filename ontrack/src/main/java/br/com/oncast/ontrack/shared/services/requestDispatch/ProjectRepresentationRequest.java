package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

public class ProjectRepresentationRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private ProjectRepresentation projectRepresentation;

	// IMPORTANT The default constructor is used by GWT. Do not remove this.
	protected ProjectRepresentationRequest() {}

	public ProjectRepresentationRequest(final ProjectRepresentation projectRepresentation) {
		this.projectRepresentation = projectRepresentation;
	}

	public ProjectRepresentation getProjectRepresentation() {
		return projectRepresentation;
	}
}
