package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;

import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.services.requestDispatch.RemoveProjectAuthorizationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.RemoveProjectAuthorizationResponse;

public class RemoveProjectAuthorizationRequestHandler implements RequestHandler<RemoveProjectAuthorizationRequest, RemoveProjectAuthorizationResponse> {

	@Override
	public RemoveProjectAuthorizationResponse handle(final RemoveProjectAuthorizationRequest request) throws Exception {
		getBusinessLogic().removeAuthorization(request.getUserId(), request.getProjectId());
		return new RemoveProjectAuthorizationResponse();
	}

	private BusinessLogic getBusinessLogic() {
		return ServerServiceProvider.getInstance().getBusinessLogic();
	}

}
