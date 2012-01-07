package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectAuthorizationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectAuthorizationResponse;

public class ProjectAuthorizationRequestHandler implements RequestHandler<ProjectAuthorizationRequest, ProjectAuthorizationResponse> {

	private static final BusinessLogic BUSINESS = ServerServiceProvider.getInstance().getBusinessLogic();

	@Override
	public ProjectAuthorizationResponse handle(final ProjectAuthorizationRequest request) throws Exception {
		BUSINESS.authorize(request.getProjectId(), request.getUserEmail());
		return new ProjectAuthorizationResponse();
	}
}
