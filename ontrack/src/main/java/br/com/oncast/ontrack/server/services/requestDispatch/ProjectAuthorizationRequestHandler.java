package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectAuthorizationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectAuthorizationResponse;

public class ProjectAuthorizationRequestHandler implements RequestHandler<ProjectAuthorizationRequest, ProjectAuthorizationResponse> {

	@Override
	public ProjectAuthorizationResponse handle(final ProjectAuthorizationRequest request) throws Exception {
		getBusinessLogic().authorize(request.getUserEmail(), request.getProjectId(), true);
		return new ProjectAuthorizationResponse();
	}

	private BusinessLogic getBusinessLogic() {
		return ServerServiceProvider.getInstance().getBusinessLogic();
	}
}
