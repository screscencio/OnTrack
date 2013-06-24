package br.com.oncast.ontrack.server.services.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@Path("/user")
public class UsersResource {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String greet() {
		System.out.println("greet()");
		return "Hello";
	}

	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {
		System.out.println("hello()");
		return "Hello";
	}

	@GET
	@Path("create")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public UUID createUser(@QueryParam("email") final String email) {
		System.out.println("createUser(" + email + ")");
		final UUID createdUserId = ServerServiceProvider.getInstance().getBusinessLogic().createUser(email);
		System.out.println(createdUserId);
		return createdUserId;
	}

}
