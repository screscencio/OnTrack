package br.com.oncast.ontrack.server.services.email;

import br.com.oncast.ontrack.server.services.CustomUrlGenerator;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

import java.util.Arrays;
import java.util.List;

public class ProjectAuthorizationMail implements OnTrackMail {

	private final ProjectRepresentation project;

	private final String currentUser;

	private final String sendTo;

	private final String generatedPassword;

	private ProjectAuthorizationMail(final ProjectRepresentation project, final String currentUser, final String sentTo, final String generatedPassword) {
		this.project = project;
		this.currentUser = currentUser;
		this.sendTo = sentTo;
		this.generatedPassword = generatedPassword;
	}

	public static ProjectAuthorizationMail getMail(final ProjectRepresentation project, final String currentUser, final String sentTo, final String generatedPassword) {
		return new ProjectAuthorizationMail(project, currentUser, sentTo, generatedPassword);
	}

	@Override
	public String getSubject() {
		return "[OnTrack] " + (isNewUser() ? "Welcome to OnTrack" : "Project Invite");
	}

	private boolean isNewUser() {
		return generatedPassword != null;
	}

	@Override
	public String getTemplatePath() {
		return "/br/com/oncast/ontrack/server/services/email/" + (isNewUser() ? "authMailNewUser" : "authMail") + ".html";
	}

	@Override
	public MailVariableValuesMap getParameters() {
		final MailVariableValuesMap context = new MailVariableValuesMap();
		context.put("projectName", project.getName());
		context.put("projectLink", CustomUrlGenerator.forProject(project));
		context.put("userEmail", sendTo);
		context.put("currentUser", currentUser);
		if (isNewUser()) context.put("generatedPassword", generatedPassword);
		return context;
	}

	@Override
	public List<String> getRecipients() {
		return Arrays.asList(sendTo);
	}
}
