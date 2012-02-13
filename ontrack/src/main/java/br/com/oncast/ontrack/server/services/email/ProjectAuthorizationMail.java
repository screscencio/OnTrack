package br.com.oncast.ontrack.server.services.email;

import javax.mail.MessagingException;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

public class ProjectAuthorizationMail {

	private final MailSender sender;
	private ProjectRepresentation project;
	private String currentUser;

	private ProjectAuthorizationMail() {
		sender = MailSender.createInstance();
	}

	public static ProjectAuthorizationMail createInstance() {
		return new ProjectAuthorizationMail();
	}

	public ProjectAuthorizationMail setProject(final ProjectRepresentation project) {
		this.project = project;
		return this;
	}

	public ProjectAuthorizationMail currentUser(final String currentUser) {
		this.currentUser = currentUser;
		return this;
	}

	public void sendTo(final String userEmail) {
		try {
			sender.subject(createAuthorizationSubject()).htmlContent(HtmlMailContent.forProjectAuthorization(userEmail, project, currentUser));
		}
		catch (final MessagingException e) {
			throw new RuntimeException("Exception configuring mail service.", e);
		}
		// sender.sendTo(userEmail);
	}

	private static String createAuthorizationSubject() {
		return "Project Invite";
	}
}
