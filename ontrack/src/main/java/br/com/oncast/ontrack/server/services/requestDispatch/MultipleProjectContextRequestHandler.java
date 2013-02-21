package br.com.oncast.ontrack.server.services.requestDispatch;

import java.util.HashSet;
import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.requestDispatch.MultipleProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.MultipleProjectContextRequestResponse;

public class MultipleProjectContextRequestHandler implements RequestHandler<MultipleProjectContextRequest, MultipleProjectContextRequestResponse> {

	@Override
	public MultipleProjectContextRequestResponse handle(final MultipleProjectContextRequest request) throws Exception {
		final Set<Project> projects = new HashSet<Project>();
		for (final ProjectRepresentation rep : request.getRequestedProjects()) {
			projects.add(ServerServiceProvider.getInstance().getBusinessLogic().loadProject(rep.getId()).getProject());
		}
		return new MultipleProjectContextRequestResponse(projects);
	}
}
