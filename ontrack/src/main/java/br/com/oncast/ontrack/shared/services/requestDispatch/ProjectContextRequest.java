package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProjectContextRequest implements DispatchRequest<ProjectContextResponse> {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private UUID requestedProjectId;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected ProjectContextRequest() {}

	public ProjectContextRequest(final UUID requestedProjectId) {
		this.requestedProjectId = requestedProjectId;
	}

	public UUID getRequestedProjectId() {
		return requestedProjectId;
	}
}
