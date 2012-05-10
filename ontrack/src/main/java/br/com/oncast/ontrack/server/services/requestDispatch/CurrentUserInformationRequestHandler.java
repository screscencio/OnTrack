package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.exceptions.authentication.NotAuthenticatedException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.requestDispatch.CurrentUserInformationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.CurrentUserInformationResponse;

public class CurrentUserInformationRequestHandler implements RequestHandler<CurrentUserInformationRequest, CurrentUserInformationResponse> {

	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();

	@Override
	public CurrentUserInformationResponse handle(final CurrentUserInformationRequest request) throws Exception {
		final User user = SERVICE_PROVIDER.getAuthenticationManager().getAuthenticatedUser();
		if (user == null) throw new NotAuthenticatedException();
		return new CurrentUserInformationResponse(user);
	}
}
