package br.com.oncast.ontrack.client.services.context;

import java.util.Set;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

public interface ProjectListChangeListener {

	void onProjectListChanged(Set<ProjectRepresentation> projectRepresentations);

	void onProjectListAvailabilityChange(boolean availability);

	void onProjectNameUpdate(ProjectRepresentation projectRepresentation);
}
