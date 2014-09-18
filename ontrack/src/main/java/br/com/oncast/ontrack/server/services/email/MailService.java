package br.com.oncast.ontrack.server.services.email;

import javax.mail.MessagingException;

public class MailService {

	public void send(final OnTrackMail mail) throws MessagingException {
		final MailSender sender = MailSender.getSender(mail);
		sender.send();
	}
}
