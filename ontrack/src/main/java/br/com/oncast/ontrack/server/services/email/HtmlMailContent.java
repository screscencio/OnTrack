package br.com.oncast.ontrack.server.services.email;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

public class HtmlMailContent {

	public static String forProjectAuthorization(final String userEmail, final ProjectRepresentation project) {
		return "You were invited to the project " + project.getName();
	}

}
