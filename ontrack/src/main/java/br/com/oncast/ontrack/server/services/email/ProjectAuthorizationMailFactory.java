package br.com.oncast.ontrack.server.services.email;

public class ProjectAuthorizationMailFactory {
	public ProjectAuthorizationMail createMail() {
		return ProjectAuthorizationMail.createInstance();
	}
}
