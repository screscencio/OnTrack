package br.com.oncast.ontrack.server.services.api;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.api.bean.AuthenticationApiRequest;
import br.com.oncast.ontrack.server.services.api.bean.AuthenticationApiResponse;
import br.com.oncast.ontrack.shared.exceptions.authentication.InvalidAuthenticationCredentialsException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/auth")
public class OnTrackAuthenticationResource {

	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public AuthenticationApiResponse authenticate(final AuthenticationApiRequest request) {
		try {
			ServerServiceProvider.getInstance().getAuthenticationManager().authenticate(request.getEmail(), request.getPassword());
			return AuthenticationApiResponse.success();
		} catch (final InvalidAuthenticationCredentialsException e) {
			return AuthenticationApiResponse.error(e.getMessage());
		}
	}

}
