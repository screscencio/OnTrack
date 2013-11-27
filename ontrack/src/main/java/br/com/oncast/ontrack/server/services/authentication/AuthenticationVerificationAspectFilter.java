package br.com.oncast.ontrack.server.services.authentication;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestFilter;
import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;

import br.com.oncast.ontrack.shared.exceptions.authentication.NotAuthenticatedException;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.DeAuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.PasswordResetRequest;

import javax.servlet.http.HttpServletRequest;

public class AuthenticationVerificationAspectFilter implements RequestFilter {

	private final AuthenticationManager authenticationManager;

	public AuthenticationVerificationAspectFilter(final AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public void doFilter(final DispatchRequest<?> request, final HttpServletRequest httpServletRequest) throws Exception {
		if (request instanceof AuthenticationRequest || request instanceof DeAuthenticationRequest || request instanceof PasswordResetRequest) return;
		if (!authenticationManager.isUserAuthenticated()) throw new NotAuthenticatedException();
	}
}
