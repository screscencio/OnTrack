package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationQuotaRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationQuotaRequestResponse;

public class ProjectCreationQuotaRequestHandler implements RequestHandler<ProjectCreationQuotaRequest, ProjectCreationQuotaRequestResponse> {

	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();

	@Override
	public ProjectCreationQuotaRequestResponse handle(final ProjectCreationQuotaRequest request) throws Exception {
		SERVICE_PROVIDER.getBusinessLogic().sendProjectCreationQuotaRequestEmail();
		return new ProjectCreationQuotaRequestResponse();
	}

}
