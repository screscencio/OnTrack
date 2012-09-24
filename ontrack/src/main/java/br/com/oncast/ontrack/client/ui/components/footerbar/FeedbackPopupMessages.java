package br.com.oncast.ontrack.client.ui.components.footerbar;

import br.com.oncast.ontrack.client.i18n.BaseMessages;

public interface FeedbackPopupMessages extends BaseMessages {

	@Description("error sending feedback")
	@DefaultMessage("Feedback was not sent. Unexpected error")
	String feedbackNotSent();

	@Description("shown when the feedback was sent")
	@DefaultMessage("Feedback Sent! Thank you very much.")
	String feedbackSent();

	@Description("indication when sending feedback")
	@DefaultMessage("Processing your Feedback...")
	String processingYourFeedback();

}
