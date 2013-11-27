package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.services.requestDispatch.ChangePasswordRequest;

public class ChangePasswordRequestHandler implements RequestHandler<ChangePasswordRequest, VoidResult> {

	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();

	@Override
	public VoidResult handle(final ChangePasswordRequest request) throws Exception {
		SERVICE_PROVIDER.getAuthenticationManager().updateCurrentUserPassword(request.getCurrentPassword(), request.getNewPassword());
		return new VoidResult();
	}

}
