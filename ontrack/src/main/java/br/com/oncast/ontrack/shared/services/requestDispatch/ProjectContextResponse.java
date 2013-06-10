package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;
import br.com.oncast.ontrack.shared.model.project.ProjectRevision;

public class ProjectContextResponse implements DispatchResponse {

	private ProjectRevision projectRevision;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ProjectContextResponse() {}

	public ProjectContextResponse(final ProjectRevision projectRevision) {
		this.projectRevision = projectRevision;
	}

	public ProjectRevision getProjectRevision() {
		return projectRevision;
	}
}
