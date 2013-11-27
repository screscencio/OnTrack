package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

public class ProjectCreationResponse implements DispatchResponse {

	private ProjectRepresentation projectRepresentation;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ProjectCreationResponse() {}

	public ProjectCreationResponse(final ProjectRepresentation projectRepresentation) {
		this.projectRepresentation = projectRepresentation;
	}

	public ProjectRepresentation getProjectRepresentation() {
		return projectRepresentation;
	}
}