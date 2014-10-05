package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.shared.exceptions.authentication.NotAuthenticatedException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.requestDispatch.CurrentUserInformationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.CurrentUserInformationResponse;

public class CurrentUserInformationRequestHandler implements RequestHandler<CurrentUserInformationRequest, CurrentUserInformationResponse> {

	private static final AuthenticationManager AUTHENTICATION_MANAGER = ServerServiceProvider.getInstance().getAuthenticationManager();

	@Override
	public CurrentUserInformationResponse handle(final CurrentUserInformationRequest request) throws Exception {
		final User user = AUTHENTICATION_MANAGER.getAuthenticatedUser();
		if (user == null) throw new NotAuthenticatedException();
		return new CurrentUserInformationResponse(user, AUTHENTICATION_MANAGER.hasPassword(user.getId()));
	}
}
