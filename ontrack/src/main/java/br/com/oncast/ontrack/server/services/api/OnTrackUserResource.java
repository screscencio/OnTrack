package br.com.oncast.ontrack.server.services.api;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.api.bean.UserCreationApiRequest;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user")
public class OnTrackUserResource {

	@POST
	@Path("create")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public UUID createUser(final UserCreationApiRequest request) {
		return ServerServiceProvider.getInstance().getBusinessLogic().createUser(request.getEmail(), request.getGlobalProfile());
	}

}
