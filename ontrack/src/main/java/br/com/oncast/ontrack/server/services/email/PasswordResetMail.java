package br.com.oncast.ontrack.server.services.email;

import java.util.Arrays;
import java.util.List;

public class PasswordResetMail implements OnTrackMail {

	private final String sendTo;
	private final String generatedPassword;

	private PasswordResetMail(final String sendTo, final String generatedPassword) {
		this.sendTo = sendTo;
		this.generatedPassword = generatedPassword;
	}

	public static PasswordResetMail getMail(final String userEmail, final String generatedPassword) {
		return new PasswordResetMail(userEmail, generatedPassword);
	}

	@Override
	public String getSubject() {
		return "[OnTrack] Password reset";
	}

	@Override
	public String getTemplatePath() {
		return "/br/com/oncast/ontrack/server/services/email/authMailPassReset.html";
	}

	@Override
	public MailVariableValuesMap getParameters() {
		final MailVariableValuesMap context = new MailVariableValuesMap();
		context.put("userEmail", sendTo);
		context.put("generatedPassword", generatedPassword);
		return context;
	}

	@Override
	public List<String> getRecipients() {
		return Arrays.asList(sendTo);
	}
}
