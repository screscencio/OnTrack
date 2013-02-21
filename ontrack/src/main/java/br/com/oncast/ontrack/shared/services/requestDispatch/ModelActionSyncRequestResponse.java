package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;

public class ModelActionSyncRequestResponse implements DispatchResponse {

	private long lastApplyedActionId;

	protected ModelActionSyncRequestResponse() {}

	public ModelActionSyncRequestResponse(final long lastApplyedActionId) {
		this.lastApplyedActionId = lastApplyedActionId;
	}

	public long getLastApplyedActionId() {
		return lastApplyedActionId;
	}
}
