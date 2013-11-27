package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchResponse;

import br.com.oncast.ontrack.shared.services.actionSync.ModelActionSyncEvent;

public class ModelActionSyncEventRequestResponse implements DispatchResponse {

	private ModelActionSyncEvent modelActionSyncEvent;

	protected ModelActionSyncEventRequestResponse() {}

	public ModelActionSyncEventRequestResponse(final ModelActionSyncEvent modelActionSyncEvent) {
		this.modelActionSyncEvent = modelActionSyncEvent;
	}

	public ModelActionSyncEvent getModelActionSyncEvent() {
		return modelActionSyncEvent;
	}

}