package br.com.oncast.ontrack.client.services.context;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

import java.util.Set;

public interface ProjectListChangeListener {

	void onProjectListChanged(Set<ProjectRepresentation> projectRepresentations);

	void onProjectListAvailabilityChange(boolean availability);

	void onProjectNameUpdate(ProjectRepresentation projectRepresentation);
}
