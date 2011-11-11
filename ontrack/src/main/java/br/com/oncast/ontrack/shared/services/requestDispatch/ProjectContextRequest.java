package br.com.oncast.ontrack.shared.services.requestDispatch;

import java.io.Serializable;

// FIXME Go all the way to the server carrying the requested project id, so that business logic can know what to search in persistence.
public class ProjectContextRequest implements Serializable {

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
