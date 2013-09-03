package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;

import br.com.oncast.ontrack.shared.model.user.Profile;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProjectAuthorizationRequest implements DispatchRequest<ProjectAuthorizationResponse> {

	private String userEmail;
	private UUID projectId;
	private Profile profile;

	// IMPORTANT The default constructor is used by GWT. Do not remove this.
	protected ProjectAuthorizationRequest() {}

	public ProjectAuthorizationRequest(final UUID projectId, final String email, final Profile profile) {
		this.projectId = projectId;
		this.userEmail = email;
		this.profile = profile;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public UUID getProjectId() {
		return projectId;
	}

	public Profile getProfile() {
		return profile;
	}

}
