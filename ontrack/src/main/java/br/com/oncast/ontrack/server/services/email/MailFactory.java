package br.com.oncast.ontrack.server.services.email;

public class MailFactory {

	public ProjectAuthorizationMail createProjectAuthorizationMail() {
		return ProjectAuthorizationMail.createInstance();
	}

	public UserQuotaRequestMail createUserQuotaRequestMail() {
		return UserQuotaRequestMail.createInstance();
	}

	public SendFeedbackMail createSendFeedbackMail() {
		return SendFeedbackMail.createInstance();
	}

	public PasswordResetMail createPasswordResetMail() {
		return PasswordResetMail.createInstance();
	}

	public WelcomeMail createWelcomeMail() {
		return WelcomeMail.createInstance();
	}

}
