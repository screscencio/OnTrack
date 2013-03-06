package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.services.requestDispatch.PasswordResetRequest;

public class PasswordResetRequestHandler implements RequestHandler<PasswordResetRequest, VoidResult> {

	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();

	@Override
	public VoidResult handle(final PasswordResetRequest request) throws Exception {
		SERVICE_PROVIDER.getAuthenticationManager().resetPassword(request.getEmail());
		return new VoidResult();
	}

}
