package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.services.requestDispatch.DeAuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.VoidResult;

public class DeAuthenticationRequestHandler implements RequestHandler<DeAuthenticationRequest, VoidResult> {

	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();

	@Override
	public VoidResult handle(final DeAuthenticationRequest request) throws Exception {
		SERVICE_PROVIDER.getAuthenticationManager().logout();
		return new VoidResult();
	}
}
