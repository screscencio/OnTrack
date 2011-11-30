package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextResponse;

public class ProjectContextRequestHandler implements RequestHandler<ProjectContextRequest, ProjectContextResponse> {

	private static final BusinessLogic BUSINESS = ServerServiceProvider.getInstance().getBusinessLogic();

	@Override
	public ProjectContextResponse handle(final ProjectContextRequest request) throws Exception {
		return new ProjectContextResponse(BUSINESS.loadProjectForClient(request));
	}

}
