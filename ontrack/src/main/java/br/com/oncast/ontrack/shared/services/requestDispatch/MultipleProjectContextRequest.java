package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

import java.util.Set;

public class MultipleProjectContextRequest implements DispatchRequest<MultipleProjectContextRequestResponse> {

	private Set<ProjectRepresentation> requestedProjects;

	public MultipleProjectContextRequest() {}

	public MultipleProjectContextRequest(final Set<ProjectRepresentation> requestedProjects) {
		this.requestedProjects = requestedProjects;
	}

	public Set<ProjectRepresentation> getRequestedProjects() {
		return requestedProjects;
	}

}
