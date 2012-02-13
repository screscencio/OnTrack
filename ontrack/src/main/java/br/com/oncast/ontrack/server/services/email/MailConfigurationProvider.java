package br.com.oncast.ontrack.server.services.email;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import br.com.oncast.ontrack.server.configuration.Configurations;

public class MailConfigurationProvider {

	static Properties configProperties() {
		final Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		return props;
	}

	static Authenticator mailAuthenticator() {
		return new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				final Configurations c = Configurations.getInstance();
				return new PasswordAuthentication(c.getEmailUsername(), c.getEmailPassword());
			}
		};
	}
}
