package br.com.oncast.ontrack.client.services.organization;

import java.util.Set;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public interface AvailableContextsListChangeListener {

	void onContextListChange(Set<ProjectContext> availableProjects);

}
