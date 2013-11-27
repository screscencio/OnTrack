package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;

import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationResponse;

public class ProjectCreationRequestHandler implements RequestHandler<ProjectCreationRequest, ProjectCreationResponse> {

	private static final BusinessLogic BUSINESS = ServerServiceProvider.getInstance().getBusinessLogic();

	@Override
	public ProjectCreationResponse handle(final ProjectCreationRequest request) throws Exception {
		return new ProjectCreationResponse(BUSINESS.createProject(request.getProjectName()));
	}

}
