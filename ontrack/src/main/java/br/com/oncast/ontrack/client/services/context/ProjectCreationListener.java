package br.com.oncast.ontrack.client.services.context;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

public interface ProjectCreationListener {

	void onProjectCreated(ProjectRepresentation projectRepresentation);

	void onUnexpectedFailure();

	void onProjectCreationFailure();
}
