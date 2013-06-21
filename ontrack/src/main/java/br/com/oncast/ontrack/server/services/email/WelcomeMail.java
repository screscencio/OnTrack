package br.com.oncast.ontrack.server.services.email;

import javax.mail.MessagingException;

public class WelcomeMail {

	private final MailSender sender;
	private String invitee;

	private WelcomeMail() {
		sender = MailSender.createInstance();
	}

	public static WelcomeMail createInstance() {
		return new WelcomeMail();
	}

	public WelcomeMail invitee(final String invitee) {
		this.invitee = invitee;
		return this;
	}

	public void sendTo(final String userEmail, final String generatedPassword) {
		try {
			final String from = invitee == null ? MailConfigurationProvider.getMailUsername()
					: invitee;
			final String mailContent = HtmlMailContent.forNewUserWelcome(userEmail, generatedPassword, from);

			sender.subject(createAuthorizationSubject()).htmlContent(mailContent).sendTo(userEmail);
		}
		catch (final MessagingException e) {
			throw new RuntimeException("Exception configuring mail service.", e);
		}
	}

	private static String createAuthorizationSubject() {
		return "Welcome to OnTrack";
	}
}
