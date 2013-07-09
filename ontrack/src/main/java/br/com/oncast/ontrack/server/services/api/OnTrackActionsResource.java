package br.com.oncast.ontrack.server.services.api;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.api.bean.ActionSendRequest;
import br.com.oncast.ontrack.server.services.api.bean.ActionSendResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/action")
public class OnTrackActionsResource {

	@POST
	@Path("send")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ActionSendResponse createUser(final ActionSendRequest request) {
		try {
			final ModelActionSyncRequest modelActionSyncRequest = new ModelActionSyncRequest(request.getProjectId(), request.getActionList()).setShouldReturnToSender(true);
			final long executedAction = ServerServiceProvider.getInstance().getBusinessLogic().handleIncomingActionSyncRequest(modelActionSyncRequest);
			return new ActionSendResponse(executedAction);
		} catch (final Exception e) {
			final Throwable exception = e;
			return new ActionSendResponse(exception);
		}

	}
}
