package br.com.oncast.ontrack.server.services.email;

import javax.mail.MessagingException;

public class MailService {

	public void send(final OnTrackMail mail) {
		try {
			final String mailContent = HtmlMailContent.getContent(mail.getTemplatePath(), mail.getParameters());
			final MailSender sender = MailSender.createInstance();
			for (final String recipient : mail.getRecipients()) {
				sender.subject(mail.getSubject()).htmlContent(mailContent).sendTo(recipient);
			}
		} catch (final MessagingException e) {
			throw new RuntimeException("Exception configuring mail service.", e);
		}
	}

}
