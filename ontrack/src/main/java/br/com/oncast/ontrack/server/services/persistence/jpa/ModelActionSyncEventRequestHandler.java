package br.com.oncast.ontrack.server.services.persistence.jpa;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncEventRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncEventRequestResponse;

public class ModelActionSyncEventRequestHandler implements RequestHandler<ModelActionSyncEventRequest, ModelActionSyncEventRequestResponse> {

	private static final ServerServiceProvider PROVIDER = ServerServiceProvider.getInstance();

	@Override
	public ModelActionSyncEventRequestResponse handle(final ModelActionSyncEventRequest request) throws Exception {
		return PROVIDER.getBusinessLogic().loadProjectActions(request.getProjectId(), request.getLastSyncId());
	}
}