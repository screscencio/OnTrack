package br.com.oncast.ontrack.shared.services.context;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

public class ProjectRemovedEvent implements ServerPushEvent {

	private static final long serialVersionUID = 1L;
	private UUID projectId;
	private String projectName;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected ProjectRemovedEvent() {}

	public ProjectRemovedEvent(final ProjectRepresentation projectRepresentation) {
		// IMPORTANT Storing the ProjectRepresentation itself returns an mysterious serialization error.
		// The id cannot be serialized. Remember to store the object parameters instead.
		projectId = projectRepresentation.getId();
		projectName = projectRepresentation.getName();
	}

	public ProjectRepresentation getProjectRepresentation() {
		return new ProjectRepresentation(projectId, projectName);
	}
}
