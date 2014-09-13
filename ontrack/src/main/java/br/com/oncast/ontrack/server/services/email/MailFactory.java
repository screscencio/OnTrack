package br.com.oncast.ontrack.server.services.email;

import br.com.oncast.ontrack.shared.services.notification.Notification;

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

	public ActivationMail createActivationMail() {
		return ActivationMail.createInstance();
	}

	public NotificationMail createNotificationMail(final Notification notification) {
		return new NotificationMail(notification);
	}

}
