package br.com.oncast.ontrack.server.services.api;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.api.bean.ProjectMemberProfileUpdateApiRequest;
import br.com.oncast.ontrack.server.services.api.bean.ProjectMemberRemoveApiRequest;
import br.com.oncast.ontrack.server.services.api.bean.ProjectsRemoveApiRequest;
import br.com.oncast.ontrack.server.services.api.bean.ProjectsRemoveApiResponse;
import br.com.oncast.ontrack.server.services.api.bean.VoidApiResponse;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRemoveProjectException;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.TeamDeclareProfileAction;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

import java.util.ArrayList;
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
	public VoidApiResponse removeProjectMember(final ProjectMemberRemoveApiRequest request) {
		try {
			ServerServiceProvider.getInstance().getBusinessLogic().removeAuthorization(request.getUserId(), request.getProjectId());
			return VoidApiResponse.success();
		} catch (final Exception e) {
			LOGGER.error("Error trying to remove the project member '" + request.getUserId() + "' from project '" + request.getProjectId() + "'", e);
			return VoidApiResponse.failed(e.getMessage());
		}
	}

	@POST
	@Path("updateMemberProfile")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public VoidApiResponse updateProjectMemberProfile(final ProjectMemberProfileUpdateApiRequest request) {
		try {
			final List<ModelAction> list = new ArrayList<ModelAction>();
			list.add(new TeamDeclareProfileAction(request.getUserId(), request.getProjectProfile()));
			final ModelActionSyncRequest actionSyncRequest = new ModelActionSyncRequest(request.getProjectId(), list);
			ServerServiceProvider.getInstance().getBusinessLogic().handleIncomingActionSyncRequest(actionSyncRequest);
			return VoidApiResponse.success();
		} catch (final Exception e) {
			LOGGER.error("Error trying to remove the project member '" + request.getUserId() + "' from project '" + request.getProjectId() + "'", e);
			return VoidApiResponse.failed(e.getMessage());
		}
	}
}
