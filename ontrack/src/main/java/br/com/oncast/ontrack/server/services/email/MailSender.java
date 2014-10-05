package br.com.oncast.ontrack.server.services.email;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

class MailSender {
	private final Session session = Session.getDefaultInstance(MailConfigurationProvider.configProperties(), MailConfigurationProvider.mailAuthenticator());

	private final MimeMessage message = new MimeMessage(session);
	private final OnTrackMail mail;

	public static MailSender getSender(final OnTrackMail mail) throws AddressException, MessagingException {
		return new MailSender(mail);
	}

	private MailSender(final OnTrackMail mail) throws AddressException, MessagingException {
		message.setFrom(new InternetAddress(MailConfigurationProvider.getMailUsername()));
		this.mail = mail;
		prepareMessage();
	}

	public MimeMessage send() throws MessagingException {
		for (final String recipient : mail.getRecipients()) {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			Transport.send(message);
		}
		return message;
	}

	private void prepareMessage() throws MessagingException {
		message.setSubject(mail.getSubject(), "UTF-8");
		final String mailContent = HtmlMailContent.getContent(mail.getTemplatePath(), mail.getParameters());
		message.setContent(htmlContent(mailContent));
	}

	private Multipart htmlContent(final String mailContent) throws MessagingException {
		final Multipart multipart = new MimeMultipart();
		final MimeBodyPart html = new MimeBodyPart();
		html.setContent(mailContent, "text/html; charset=UTF-8");
		multipart.addBodyPart(html);
		return multipart;
	}
}