package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class RemoveProjectAuthorizationRequest implements DispatchRequest<RemoveProjectAuthorizationResponse> {

	private UUID userId;
	private UUID projectId;

	// IMPORTANT The default constructor is used by GWT. Do not remove this.
	protected RemoveProjectAuthorizationRequest() {}

	public RemoveProjectAuthorizationRequest(final UUID projectId, final UUID userId) {
		this.projectId = projectId;
		this.userId = userId;
	}

	public UUID getUserId() {
		return userId;
	}

	public UUID getProjectId() {
		return projectId;
	}

}
