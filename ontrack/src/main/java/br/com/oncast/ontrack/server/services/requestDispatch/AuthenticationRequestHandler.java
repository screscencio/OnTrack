package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationResponse;

public class AuthenticationRequestHandler implements RequestHandler<AuthenticationRequest, AuthenticationResponse> {

	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();

	@Override
	public AuthenticationResponse handle(final AuthenticationRequest request) throws Exception {
		final User user = SERVICE_PROVIDER.getAuthenticationManager().authenticate(request.getEmail(), request.getPassword());
		return new AuthenticationResponse(user);
	}

}
