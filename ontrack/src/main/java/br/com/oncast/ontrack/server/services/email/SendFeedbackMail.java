package br.com.oncast.ontrack.server.services.email;

import javax.mail.MessagingException;

public class SendFeedbackMail {

	private final MailSender sender;
	private String currentUser;
	private String feedbackMessage;

	private SendFeedbackMail() {
		sender = MailSender.createInstance();
	}

	public static SendFeedbackMail createInstance() {
		return new SendFeedbackMail();
	}

	public SendFeedbackMail currentUser(final String currentUser) {
		this.currentUser = currentUser;
		return this;
	}

	public SendFeedbackMail feedbackMessage(final String feedbackMessage) {
		this.feedbackMessage = feedbackMessage;
		return this;
	}

	public void send() {
		try {
			final String mailContent = HtmlMailContent.forSendFeedback(currentUser, feedbackMessage);
			sender.subject(getSubject()).replyTo(currentUser).htmlContent(mailContent).sendToDefaultEmail();
		}
		catch (final MessagingException e) {
			throw new RuntimeException("Exception configuring mail service.", e);
		}
	}

	private String getSubject() {
		return "Feedback from " + currentUser;
	}
}
