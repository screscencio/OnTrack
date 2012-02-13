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

public class MailSender {
	private final Session session = Session.getDefaultInstance(MailConfigurationProvider.configProperties(), MailConfigurationProvider.mailAuthenticator());

	private final MimeMessage message;

	private MailSender() throws MessagingException {
		message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress("robot@oncast.com.br"));
		}
		catch (final AddressException e) {
			throw new RuntimeException("Invalid sender e-mail.", e);
		}
	}

	public static MailSender createInstance() {
		try {
			return new MailSender();
		}
		catch (final MessagingException e) {
			throw new RuntimeException("Exception configuring mail service.", e);
		}
	}

	public MailSender subject(final String string) throws MessagingException {
		message.setSubject(string, "UTF-8");
		return this;
	}

	public MailSender htmlContent(final String htmlContent) throws MessagingException {
		final Multipart multipart = new MimeMultipart();
		final MimeBodyPart html = new MimeBodyPart();
		html.setContent(htmlContent, "text/html");
		multipart.addBodyPart(html);
		message.setContent(multipart);
		return this;
	}

	public void sendTo(final String email) {
		try {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
			Transport.send(message);
		}
		catch (final AddressException e) {
			throw new RuntimeException(String.format("User has an invalid e-mail: %s.", email), e);
		}
		catch (final MessagingException e) {
			throw new RuntimeException("Error sending e-mail.", e);
		}
	}
}
