package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProjectContextRequest implements DispatchRequest<ProjectContextResponse> {

	private static final long serialVersionUID = 1L;
	private long requestedProjectId;
	private UUID clientId;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected ProjectContextRequest() {}

	public ProjectContextRequest(final UUID clientId, final long requestedProjectId) {
		this.clientId = clientId;
		this.requestedProjectId = requestedProjectId;
	}

	public long getRequestedProjectId() {
		return requestedProjectId;
	}

	public UUID getClientId() {
		return clientId;
	}
}
