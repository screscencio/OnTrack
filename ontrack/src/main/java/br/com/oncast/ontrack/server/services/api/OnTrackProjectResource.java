package br.com.oncast.ontrack.server.services.api;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.api.bean.ProjectContextApiRequest;
import br.com.oncast.ontrack.server.services.api.bean.ProjectContextApiResponse;
import br.com.oncast.ontrack.server.services.api.bean.ProjectsListApiResponse;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectRevision;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

@Path("/project")
public class OnTrackProjectResource {

	private static final Logger LOGGER = Logger.getLogger(OnTrackProjectResource.class);

	@POST
	@Path("get")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ProjectContextApiResponse getProjectContext(final ProjectContextApiRequest request) {
		try {
			System.out.println("getProjectContext");
			System.out.println(request.getProjectId());
			final ProjectRevision revision = ServerServiceProvider.getInstance().getBusinessLogic().loadProject(request.getProjectId());
			System.out.println(revision.getRevision());
			return new ProjectContextApiResponse(new ProjectContext(revision.getProject()), revision.getRevision());
		} catch (final Exception e) {
			LOGGER.error("Error trying to retrieve the context for project '" + request.getProjectId() + "'", e);
			return new ProjectContextApiResponse(e);
		}
	}

	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ProjectsListApiResponse listProjects() {
		try {
			final List<ProjectRepresentation> projectList = ServerServiceProvider.getInstance().getBusinessLogic().retrieveCurrentUserProjectList();
			return new ProjectsListApiResponse(projectList);
		} catch (final Exception e) {
			LOGGER.error("Error trying to retrieve the projects list", e);
			return new ProjectsListApiResponse(e);
		}
	}
}
