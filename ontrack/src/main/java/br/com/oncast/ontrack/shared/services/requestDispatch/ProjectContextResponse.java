package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;
import br.com.oncast.ontrack.shared.model.project.Project;

public class ProjectContextResponse implements DispatchResponse {

	private Project project;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ProjectContextResponse() {}

	public ProjectContextResponse(final Project project) {
		this.project = project;
	}

	public Project getProject() {
		return project;
	}
}
