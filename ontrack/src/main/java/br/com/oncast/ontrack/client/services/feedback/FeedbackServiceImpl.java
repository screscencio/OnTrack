package br.com.oncast.ontrack.client.services.feedback;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;

import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationQuotaRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationQuotaRequestResponse;

public class FeedbackServiceImpl implements FeedbackService {

	private final DispatchService requestDispatchService;

	public FeedbackServiceImpl(final DispatchService requestDispatchService) {
		this.requestDispatchService = requestDispatchService;
	}

	@Override
	public void requestProjectCreationQuota(final ProjectCreationQuotaRequisitionCallback callback) {
		requestDispatchService.dispatch(new ProjectCreationQuotaRequest(),
				new DispatchCallback<ProjectCreationQuotaRequestResponse>() {

					@Override
					public void onSuccess(final ProjectCreationQuotaRequestResponse response) {
						callback.onRequestSentSucessfully();
					}

					@Override
					public void onTreatedFailure(final Throwable caught) {}

					@Override
					public void onUntreatedFailure(final Throwable caught) {
						callback.onUnexpectedFailure(caught);
					}
				});
	}

	@Override
	public void sendFeedback(final String feedbackText, final SendFeedbackCallback callback) {
		requestDispatchService.dispatch(new SendFeedbackRequest(feedbackText),
				new DispatchCallback<VoidResult>() {

					@Override
					public void onSuccess(final VoidResult result) {
						callback.onFeedbackSentSucessfully();
					}

					@Override
					public void onTreatedFailure(final Throwable caught) {}

					@Override
					public void onUntreatedFailure(final Throwable caught) {
						caught.printStackTrace();
						callback.onUnexpectedFailure(caught);
					}

				});
	}

}
