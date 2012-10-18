package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnection;
import br.com.oncast.ontrack.server.services.session.Session;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.UserScopeSelectionMulticastRequest;

public class UserScopeSelectionMulticastRequestHandler implements RequestHandler<UserScopeSelectionMulticastRequest, VoidResult> {

	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();

	@Override
	public VoidResult handle(final UserScopeSelectionMulticastRequest request) throws Exception {
		final Session session = SERVICE_PROVIDER.getSessionManager().getCurrentSession();
		final ServerPushConnection clientId = session.getThreadLocalClientId();
		final UUID userId = session.getAuthenticatedUser().getId();
		SERVICE_PROVIDER.getUsersStatusManager().onUserSelectedScope(userId, clientId, request.getSelectedScopeId());
		return new VoidResult();
	}
}
