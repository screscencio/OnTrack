package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.user.UsersStatusManager;
import br.com.oncast.ontrack.shared.services.requestDispatch.UsersStatusRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.UsersStatusRequestResponse;

public class UsersStatusRequestHandler implements RequestHandler<UsersStatusRequest, UsersStatusRequestResponse> {

	@Override
	public UsersStatusRequestResponse handle(final UsersStatusRequest request) throws Exception {
		final UsersStatusManager usersStatusManager = ServerServiceProvider.getInstance().getUsersStatusManager();
		return new UsersStatusRequestResponse(usersStatusManager.getUsersAtProject(request.getProjectId()), usersStatusManager.getOnlineUsers(request
				.getProjectId()));
	}
}
