package br.com.oncast.ontrack.server.services.email;

import br.com.oncast.ontrack.server.services.CustomUrlGenerator;

public class WelcomeMail implements OnTrackMail {

	private final String invitee;

	private final String userEmail;

	private final String generatedPassword;

	private WelcomeMail(final String invitee, final String userEmail, final String generatedPassword) {
		this.invitee = invitee;
		this.userEmail = userEmail;
		this.generatedPassword = generatedPassword;
	}

	public static WelcomeMail getMail(final String invitee, final String userEmail, final String generatedPassword) {
		return new WelcomeMail(invitee, userEmail, generatedPassword);
	}

	@Override
	public String getSubject() {
		return "[OnTrack] Welcome";
	}

	@Override
	public String getTemplatePath() {
		return "/br/com/oncast/ontrack/server/services/email/welcomeToNewUser.html";
	}

	@Override
	public MailVariableValuesMap getParameters() {
		final MailVariableValuesMap context = new MailVariableValuesMap();
		context.put("projectLink", CustomUrlGenerator.getApplicationUrl());
		context.put("userEmail", userEmail);
		context.put("currentUser", invitee);
		context.put("generatedPassword", generatedPassword);
		return context;
	}

	@Override
	public String getSendTo() {
		return userEmail;
	}
}
