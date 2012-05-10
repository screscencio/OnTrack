package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.drycode.api.web.gwt.dispatchService.server.RequestHandler;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;
import br.com.oncast.ontrack.client.services.feedback.SendFeedbackRequest;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;

public class SendFeedbackRequestHandler implements RequestHandler<SendFeedbackRequest, VoidResult> {

	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();

	@Override
	public VoidResult handle(final SendFeedbackRequest request) throws Exception {
		SERVICE_PROVIDER.getBusinessLogic().sendFeedbackEmail(request.getFeedbackText());
		return new VoidResult();
	}

}
