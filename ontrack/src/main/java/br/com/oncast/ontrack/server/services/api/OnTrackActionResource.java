package br.com.oncast.ontrack.server.services.api;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.api.bean.ActionExecutionApiRequest;
import br.com.oncast.ontrack.server.services.api.bean.ActionExecutionApiResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/action")
public class OnTrackActionResource {

	@POST
	@Path("execute")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ActionExecutionApiResponse executeAction(final ActionExecutionApiRequest request) {
		try {
			final ModelActionSyncRequest modelActionSyncRequest = new ModelActionSyncRequest(request.getProjectId(), request.getActionList()).setShouldReturnToSender(true);
			final long projectRevision = ServerServiceProvider.getInstance().getBusinessLogic().handleIncomingActionSyncRequest(modelActionSyncRequest);
			return new ActionExecutionApiResponse(projectRevision);
		} catch (final Exception e) {
			return new ActionExecutionApiResponse(e);
		}

	}

}
