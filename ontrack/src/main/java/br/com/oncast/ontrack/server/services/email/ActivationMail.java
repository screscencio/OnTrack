package br.com.oncast.ontrack.server.services.email;

import javax.mail.MessagingException;

public class ActivationMail {

	private final MailSender sender;
	private String invitee;

	private ActivationMail() {
		sender = MailSender.createInstance();
	}

	public static ActivationMail createInstance() {
		return new ActivationMail();
	}

	public ActivationMail invitee(final String invitee) {
		this.invitee = invitee;
		return this;
	}

	public void sendTo(final String userEmail, final String accessToken) {
		try {
			final String from = invitee == null ? MailConfigurationProvider.getMailUsername() : invitee;
			final String mailContent = HtmlMailContent.forTrialUserWelcome(userEmail, accessToken, from);
			sender.subject("Bem-vindo ao OnTrack | Welcome to OnTrack").htmlContent(mailContent).sendTo(userEmail);
		} catch (final MessagingException e) {
			throw new RuntimeException("Exception configuring mail service.", e);
		}
	}

}
