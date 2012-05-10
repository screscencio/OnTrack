package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.authorization.AuthorizationManager;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectAuthorizationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectAuthorizationResponse;

public class ProjectAuthorizationRequestHandler implements RequestHandler<ProjectAuthorizationRequest, ProjectAuthorizationResponse> {

	private static final AuthorizationManager AUTHORIZATION_MANAGER = ServerServiceProvider.getInstance().getAuthorizationManager();

	@Override
	public ProjectAuthorizationResponse handle(final ProjectAuthorizationRequest request) throws Exception {
		AUTHORIZATION_MANAGER.authorize(request.getProjectId(), request.getUserEmail(), true);
		return new ProjectAuthorizationResponse();
	}
}
