package br.com.oncast.ontrack.client.services.authentication;

import br.com.drycode.api.web.gwt.dispatchService.client.FailureHandler;
import br.com.oncast.ontrack.shared.exceptions.authentication.NotAuthenticatedException;

public class NotAuthenticatedExceptionGlobalHandler implements FailureHandler<NotAuthenticatedException> {

	private final AuthenticationService authenticationService;

	public NotAuthenticatedExceptionGlobalHandler(final AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	public void handle(final NotAuthenticatedException caught) {
		authenticationService.onUserLogout();
	}
}