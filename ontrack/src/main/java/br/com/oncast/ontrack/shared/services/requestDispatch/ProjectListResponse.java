package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

import java.util.List;

public class ProjectListResponse implements DispatchResponse {

	private List<ProjectRepresentation> projectList;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ProjectListResponse() {}

	public ProjectListResponse(final List<ProjectRepresentation> projectList) {
		this.projectList = projectList;
	}

	public List<ProjectRepresentation> getProjectList() {
		return projectList;
	}
}