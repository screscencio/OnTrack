package br.com.oncast.ontrack.server.services.api;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.api.bean.ProjectMemberRemoveApiRequest;
import br.com.oncast.ontrack.server.services.api.bean.ProjectMemberRemoveApiResponse;
import br.com.oncast.ontrack.server.services.api.bean.ProjectsRemoveApiRequest;
import br.com.oncast.ontrack.server.services.api.bean.ProjectsRemoveApiResponse;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRemoveProjectException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

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
	@Path("remove")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ProjectsRemoveApiResponse removeProject(final ProjectsRemoveApiRequest request) {
		final ProjectsRemoveApiResponse response = new ProjectsRemoveApiResponse();
		for (final UUID projectId : request) {
			try {
				ServerServiceProvider.getInstance().getBusinessLogic().removeProject(projectId);
				response.removedSuccessfully(projectId);
			} catch (final UnableToRemoveProjectException e) {
				LOGGER.error("Error trying to remove the project '" + projectId + "'", e);
				response.failedToRemove(projectId, e);
			}
		}
		return response;
	}

	@POST
	@Path("removeMember")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ProjectMemberRemoveApiResponse removeProjectMember(final ProjectMemberRemoveApiRequest request) {
		try {
			ServerServiceProvider.getInstance().getBusinessLogic().removeAuthorization(request.getUserId(), request.getProjectId());
			return ProjectMemberRemoveApiResponse.success();
		} catch (final Exception e) {
			LOGGER.error("Error trying to remove the project member '" + request.getUserId() + "' from project '" + request.getProjectId() + "'", e);
			return ProjectMemberRemoveApiResponse.failed(e.getMessage());
		}
	}
}
