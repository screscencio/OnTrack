package br.com.oncast.ontrack.server.services.email;

import br.com.oncast.ontrack.server.services.CustomUrlGenerator;

public class ActivationMail implements OnTrackMail {

	private String invitee;

	private final String accessToken;

	private final String userEmail;

	private ActivationMail(final String accessToken, final String userEmail) {
		this.accessToken = accessToken;
		this.userEmail = userEmail;
	}

	public static ActivationMail getMail(final String accessToken, final String userEmail) {
		return new ActivationMail(accessToken, userEmail);
	}

	public ActivationMail invitee(final String invitee) {
		this.invitee = invitee;
		return this;
	}

	@Override
	public String getSubject() {
		return "Bem-vindo ao OnTrack | Welcome to OnTrack";
	}

	@Override
	public String getTemplatePath() {
		return "/br/com/oncast/ontrack/server/services/email/welcomeToTrialUser.html";
	}

	@Override
	public MailVariableValuesMap getParameters() {
		final MailVariableValuesMap context = new MailVariableValuesMap();
		context.put("projectLink", CustomUrlGenerator.getApplicationUrl() + "onboarding/access/" + accessToken);
		context.put("userEmail", userEmail);
		context.put("currentUser", getFrom());
		return context;
	}

	@Override
	public String getSendTo() {
		return userEmail;
	}

	public String getFrom() {
		return invitee == null ? MailConfigurationProvider.getMailUsername() : invitee;
	}

}
