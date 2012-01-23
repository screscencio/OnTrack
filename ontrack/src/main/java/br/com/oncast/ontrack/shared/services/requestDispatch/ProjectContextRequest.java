package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;

public class ProjectContextRequest implements DispatchRequest<ProjectContextResponse> {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private long requestedProjectId;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected ProjectContextRequest() {}

	public ProjectContextRequest(final long requestedProjectId) {
		this.requestedProjectId = requestedProjectId;
	}

	public long getRequestedProjectId() {
		return requestedProjectId;
	}
}
