package br.com.oncast.ontrack.server.services.email;

public class FeedbackMailFactory {
	public UserQuotaRequestMail createUserQuotaRequestMail() {
		return UserQuotaRequestMail.createInstance();
	}

	public SendFeedbackMail createSendFeedbackMail() {
		return SendFeedbackMail.createInstance();
	}

}
