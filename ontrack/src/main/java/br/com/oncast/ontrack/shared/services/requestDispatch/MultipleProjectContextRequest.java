package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

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
