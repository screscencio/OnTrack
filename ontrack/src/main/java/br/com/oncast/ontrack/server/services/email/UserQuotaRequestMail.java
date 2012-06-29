package br.com.oncast.ontrack.server.services.email;

import javax.mail.MessagingException;

public class UserQuotaRequestMail {

	private final MailSender sender;
	private String currentUser;

	private UserQuotaRequestMail() {
		sender = MailSender.createInstance();
	}

	public static UserQuotaRequestMail createInstance() {
		return new UserQuotaRequestMail();
	}

	public UserQuotaRequestMail currentUser(final String currentUser) {
		this.currentUser = currentUser;
		return this;
	}

	public void send() {
		try {
			final String mailContent = HtmlMailContent.forProjectCreationQuotaRequest(currentUser);
			sender.subject(createProjectCreationQuotaRequestSubject()).replyTo(currentUser).htmlContent(mailContent).sendToDefaultEmail();
		}
		catch (final MessagingException e) {
			throw new RuntimeException("Exception configuring mail service.", e);
		}
	}

	private String createProjectCreationQuotaRequestSubject() {
		return "Project Creation Quota Request";
	}
}
