package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.user.UserDataManager;
import br.com.oncast.ontrack.shared.services.requestDispatch.UserDataUpdateRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.UserDataUpdateRequestResponse;

public class UserDataUpdateRequestHandler implements RequestHandler<UserDataUpdateRequest, UserDataUpdateRequestResponse> {

	@Override
	public UserDataUpdateRequestResponse handle(final UserDataUpdateRequest request) throws Exception {
		final UserDataManager userDataManager = ServerServiceProvider.getInstance().getUsersDataManager();

		return new UserDataUpdateRequestResponse(userDataManager.updateUserInformation(request.getUser()));
	}
}
