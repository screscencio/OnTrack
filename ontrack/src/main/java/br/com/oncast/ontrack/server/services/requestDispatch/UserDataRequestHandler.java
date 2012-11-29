package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.user.UserDataManager;
import br.com.oncast.ontrack.shared.services.requestDispatch.UserDataRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.UserDataRequestResponse;

public class UserDataRequestHandler implements RequestHandler<UserDataRequest, UserDataRequestResponse> {

	@Override
	public UserDataRequestResponse handle(final UserDataRequest request) throws Exception {
		final UserDataManager userDataManager = ServerServiceProvider.getInstance().getUsersDataManager();

		return new UserDataRequestResponse(userDataManager.findAllUsersForProjectId(request.getProjectId()));
	}

}
