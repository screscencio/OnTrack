package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UsersStatusRequest implements DispatchRequest<UsersStatusRequestResponse> {

	private UUID projectId;

	protected UsersStatusRequest() {}

	public UsersStatusRequest(final UUID projectId) {
		this.projectId = projectId;
	}

	public UUID getProjectId() {
		return projectId;
	}

}
