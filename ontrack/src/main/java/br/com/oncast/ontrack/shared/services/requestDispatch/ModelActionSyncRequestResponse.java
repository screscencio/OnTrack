package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ModelActionSyncRequestResponse implements DispatchResponse {

	private long lastApplyedActionId;

	private UUID projectId;

	protected ModelActionSyncRequestResponse() {}

	public ModelActionSyncRequestResponse(final UUID projectId, final long lastApplyedActionId) {
		this.projectId = projectId;
		this.lastApplyedActionId = lastApplyedActionId;
	}

	public long getLastApplyedActionId() {
		return lastApplyedActionId;
	}

	public UUID getProjectId() {
		return projectId;
	}
}