package br.com.oncast.ontrack.client.services.context;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

// FIXME Is this class necessary? Can we use only ProjectContextProvider everywhere?
public class ProjectRepresentationProvider {

	ProjectRepresentation projectRepresentation;

	public ProjectRepresentation getCurrentProjectRepresentation() {
		// FIXME Analyze if it is better to throw a new exception or to create a representation of a non existing / non persisted project (id = 0).
		if (projectRepresentation == null) return new ProjectRepresentation(0, "null");
		return projectRepresentation;
	}

	public void setProjectRepresentation(final ProjectRepresentation projectRepresentation) {
		this.projectRepresentation = projectRepresentation;
	}
}
