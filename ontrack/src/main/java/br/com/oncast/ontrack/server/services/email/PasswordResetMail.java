package br.com.oncast.ontrack.server.services.email;

import javax.mail.MessagingException;

public class PasswordResetMail {

	private final MailSender sender;

	private PasswordResetMail() {
		sender = MailSender.createInstance();
	}

	public static PasswordResetMail createInstance() {
		return new PasswordResetMail();
	}

	public void send(final String userEmail, final String generatedPassword) {
		try {
			final String mailContent = HtmlMailContent.forPasswordReset(userEmail, generatedPassword);
			sender.subject(createPasswordResetSubject()).htmlContent(mailContent).sendTo(userEmail);
		}
		catch (final MessagingException e) {
			throw new RuntimeException("Exception configuring mail service.", e);
		}
	}

	private static String createPasswordResetSubject() {
		return "Password reset";
	}
}
