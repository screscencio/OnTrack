package br.com.oncast.ontrack.server.services.authentication;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestFilter;
import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.oncast.ontrack.shared.exceptions.authentication.NotAuthenticatedException;

public class AuthenticationVerificationAspectFilter implements RequestFilter {

	private final AuthenticationManager authenticationManager;

	public AuthenticationVerificationAspectFilter(final AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public void doFilter(final DispatchRequest<?> request) throws Exception {
		if (!authenticationManager.isUserAuthenticated()) throw new NotAuthenticatedException();
	}
}
