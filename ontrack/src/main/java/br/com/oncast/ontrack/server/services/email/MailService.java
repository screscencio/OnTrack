package br.com.oncast.ontrack.server.services.email;

import javax.mail.MessagingException;

public class MailService {

	public void send(final OnTrackMail mail) {
		try {
			final String mailContent = HtmlMailContent.getContent(mail.getTemplatePath(), mail.getParameters());
			final MailSender sender = MailSender.createInstance();
			sender.subject(mail.getSubject()).htmlContent(mailContent).sendTo(mail.getSendTo());
		} catch (final MessagingException e) {
			throw new RuntimeException("Exception configuring mail service.", e);
		}
	}

}
