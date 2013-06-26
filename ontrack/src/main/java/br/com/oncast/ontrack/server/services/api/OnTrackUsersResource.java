package br.com.oncast.ontrack.server.services.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@Path("/user")
public class OnTrackUsersResource {

	@GET
	@Path("create")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public UUID createUser(@QueryParam("email") final String email) {
		return ServerServiceProvider.getInstance().getBusinessLogic().createUser(email);
	}

}
