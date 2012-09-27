package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.services.requestDispatch.ActiveUsersRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ActiveUsersRequestResponse;

public class ActiveUsersRequestHandler implements RequestHandler<ActiveUsersRequest, ActiveUsersRequestResponse> {

	@Override
	public ActiveUsersRequestResponse handle(final ActiveUsersRequest request) throws Exception {
		return new ActiveUsersRequestResponse(ServerServiceProvider.getInstance().getClientManagerService().getActiveUsers(request.getProjectId()));
	}
}
