package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UserDataRequest implements DispatchRequest<UserDataRequestResponse> {

	private UUID projectId;

	protected UserDataRequest() {}

	public UserDataRequest(final UUID projectId) {
		this.projectId = projectId;
	}

	public UUID getProjectId() {
		return projectId;
	}
}
