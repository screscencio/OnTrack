package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ModelActionSyncEventRequest implements DispatchRequest<ModelActionSyncEventRequestResponse> {

	private long lastSyncId;
	private UUID projectId;

	protected ModelActionSyncEventRequest() {}

	public ModelActionSyncEventRequest(final UUID projectId, final long lastSyncId) {
		this.projectId = projectId;
		this.lastSyncId = lastSyncId;
	}

	public UUID getProjectId() {
		return projectId;
	}

	public long getLastSyncId() {
		return lastSyncId;
	}
}