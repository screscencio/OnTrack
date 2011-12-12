package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.DispatchResponseObjectContainer;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationRequest;

public class AuthenticationRequestHandler implements RequestHandler<AuthenticationRequest, DispatchResponseObjectContainer<User>> {

	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();

	@Override
	public DispatchResponseObjectContainer<User> handle(final AuthenticationRequest request) throws Exception {
		final User user = SERVICE_PROVIDER.getAuthenticationManager().authenticate(request.getEmail(), request.getPassword());
		return new DispatchResponseObjectContainer<User>(user);
	}

}
