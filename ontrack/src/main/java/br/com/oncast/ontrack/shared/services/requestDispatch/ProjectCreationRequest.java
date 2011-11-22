package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.io.Serializable;

public class ProjectCreationRequest implements Serializable {

	private static final long serialVersionUID = 1L;
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
