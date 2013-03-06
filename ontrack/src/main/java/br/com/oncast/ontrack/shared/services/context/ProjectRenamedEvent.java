package br.com.oncast.ontrack.shared.services.context;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

public class ProjectRenamedEvent implements ServerPushEvent {

	private static final long serialVersionUID = 1L;
	private ProjectRepresentation projectRepresentation;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected ProjectRenamedEvent() {}

	public ProjectRenamedEvent(final ProjectRepresentation projectRepresentation) {
		this.projectRepresentation = projectRepresentation;
	}

	public ProjectRepresentation getProjectRepresentation() {
		return projectRepresentation;
	}
}
