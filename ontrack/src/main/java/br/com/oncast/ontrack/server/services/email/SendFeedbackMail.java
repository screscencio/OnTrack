package br.com.oncast.ontrack.server.services.email;

public class SendFeedbackMail implements OnTrackMail {

	private final String currentUser;

	private final String feedbackMessage;

	private SendFeedbackMail(final String currentUser, final String feedbackMessage) {
		this.currentUser = currentUser;
		this.feedbackMessage = feedbackMessage;
	}

	public static SendFeedbackMail getMail(final String currentUser, final String feedbackMessage) {
		return new SendFeedbackMail(currentUser, feedbackMessage);
	}

	@Override
	public String getSubject() {
		return "[OnTrack] Feedback from " + currentUser;
	}

	@Override
	public String getTemplatePath() {
		return "/br/com/oncast/ontrack/server/services/email/sendFeedback.html";
	}

	@Override
	public MailVariableValuesMap getParameters() {
		final MailVariableValuesMap context = new MailVariableValuesMap();
		context.put("currentUser", currentUser);
		context.put("feedbackMessage", feedbackMessage);
		return context;
	}

	@Override
	public String getSendTo() {
		return MailConfigurationProvider.getMailUsername();
	}
}
