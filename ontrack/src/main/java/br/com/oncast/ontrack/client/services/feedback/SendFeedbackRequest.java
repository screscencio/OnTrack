package br.com.oncast.ontrack.client.services.feedback;

import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;

public class SendFeedbackRequest implements DispatchRequest<VoidResult> {

	private String feedbackText;

	// IMPORTANT needed by serialization
	protected SendFeedbackRequest() {}

	public SendFeedbackRequest(final String feedbackText) {
		this.setFeedbackText(feedbackText);
	}

	public String getFeedbackText() {
		return feedbackText;
	}

	public void setFeedbackText(final String feedbackText) {
		this.feedbackText = feedbackText;
	}

}
