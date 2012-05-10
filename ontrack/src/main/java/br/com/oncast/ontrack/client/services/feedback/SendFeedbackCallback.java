package br.com.oncast.ontrack.client.services.feedback;

public interface SendFeedbackCallback {

	void onFeedbackSentSucessfully();

	void onUnexpectedFailure(Throwable caught);

}
