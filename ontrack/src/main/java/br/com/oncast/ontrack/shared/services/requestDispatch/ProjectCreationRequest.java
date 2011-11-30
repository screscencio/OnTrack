package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;

public class ProjectCreationRequest implements DispatchRequest<ProjectCreationResponse> {

	private String projectName;

	// IMPORTANT The default constructor is used by GWT. Do not remove this.
	protected ProjectCreationRequest() {}

	public ProjectCreationRequest(final String projectName) {
		this.projectName = projectName;
	}

	public String getProjectName() {
		return projectName;
	}
}
