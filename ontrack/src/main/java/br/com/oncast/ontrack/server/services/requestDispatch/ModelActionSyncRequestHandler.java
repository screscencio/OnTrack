package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequestResponse;

public class ModelActionSyncRequestHandler implements RequestHandler<ModelActionSyncRequest, ModelActionSyncRequestResponse> {

	private static final BusinessLogic BUSINESS = ServerServiceProvider.getInstance().getBusinessLogic();

	@Override
	public ModelActionSyncRequestResponse handle(final ModelActionSyncRequest request) throws Exception {
		final long lastApplyedActionId = BUSINESS.handleIncomingActionSyncRequest(request);
		return new ModelActionSyncRequestResponse(lastApplyedActionId);
	}

}
