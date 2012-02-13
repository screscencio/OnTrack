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

	public void sendTo(final String userEmail, final boolean isNewUser) {
		try {
			String mailContent;
			if (isNewUser) mailContent = HtmlMailContent.forNewUserProjectAuthorization(userEmail, project, currentUser);
			else mailContent = HtmlMailContent.forProjectAuthorization(userEmail, project, currentUser);

			sender.subject(createAuthorizationSubject()).htmlContent(mailContent).sendTo(userEmail);
		}
		catch (final MessagingException e) {
			throw new RuntimeException("Exception configuring mail service.", e);
		}
	}

	private static String createAuthorizationSubject() {
		return "Project Invite";
	}
}
