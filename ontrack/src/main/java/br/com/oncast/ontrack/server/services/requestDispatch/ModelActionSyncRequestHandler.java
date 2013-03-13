package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;
import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

public class ModelActionSyncRequestHandler implements RequestHandler<ModelActionSyncRequest, VoidResult> {

	private static final BusinessLogic BUSINESS = ServerServiceProvider.getInstance().getBusinessLogic();

	@Override
	public VoidResult handle(final ModelActionSyncRequest request) throws Exception {
		BUSINESS.handleIncomingActionSyncRequest(request);
		return new VoidResult();
	}

}
