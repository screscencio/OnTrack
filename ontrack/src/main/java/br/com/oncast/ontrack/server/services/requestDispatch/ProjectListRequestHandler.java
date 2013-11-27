package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;

import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListResponse;

public class ProjectListRequestHandler implements RequestHandler<ProjectListRequest, ProjectListResponse> {

	private static final BusinessLogic BUSINESS = ServerServiceProvider.getInstance().getBusinessLogic();

	@Override
	public ProjectListResponse handle(final ProjectListRequest request) throws Exception {
		return new ProjectListResponse(BUSINESS.retrieveCurrentUserProjectList());
	}

}
