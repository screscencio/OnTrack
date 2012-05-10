package br.com.oncast.ontrack.client.services;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.services.authorization.AuthorizationExceptionGlobalHandler;
import br.com.oncast.ontrack.client.services.authorization.AuthorizationService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;

public class AuthorizationServiceImpl implements AuthorizationService {

	private final ApplicationPlaceController applicationPlaceController;
	private final DispatchService dispatchService;

	public AuthorizationServiceImpl(final DispatchService dispatchService, final ApplicationPlaceController applicationPlaceController) {
		this.dispatchService = dispatchService;
		this.applicationPlaceController = applicationPlaceController;
	}

	@Override
	public void registerAuthorizationExceptionGlobalHandler() {
		dispatchService.addFailureHandler(AuthorizationException.class, new AuthorizationExceptionGlobalHandler(applicationPlaceController));
	}

}
