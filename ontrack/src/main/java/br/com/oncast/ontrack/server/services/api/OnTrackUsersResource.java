package br.com.oncast.ontrack.server.services.api;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.api.bean.UserCreationRequest;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user")
public class OnTrackUsersResource {

	@POST
	@Path("create")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public UUID createUser(final UserCreationRequest request) {
		return ServerServiceProvider.getInstance().getBusinessLogic().createUser(request.getEmail(), request.isSuperUser());
	}

}
