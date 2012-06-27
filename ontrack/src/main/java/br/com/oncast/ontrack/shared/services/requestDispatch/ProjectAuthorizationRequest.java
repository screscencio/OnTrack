package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProjectAuthorizationRequest implements DispatchRequest<ProjectAuthorizationResponse> {

	private String userEmail;
	private UUID projectId;

	// IMPORTANT The default constructor is used by GWT. Do not remove this.
	protected ProjectAuthorizationRequest() {}

	public ProjectAuthorizationRequest(final UUID projectId, final String email) {
		this.projectId = projectId;
		this.userEmail = email;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public UUID getProjectId() {
		return projectId;
	}

}
