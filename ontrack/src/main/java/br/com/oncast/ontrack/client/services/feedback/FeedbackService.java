package br.com.oncast.ontrack.client.services.feedback;

public interface FeedbackService {

	void requestProjectCreationQuota(ProjectCreationQuotaRequisitionCallback projectCreationQuotaRequisitionCallback);

	void sendFeedback(String feedbackText, SendFeedbackCallback callback);

}
