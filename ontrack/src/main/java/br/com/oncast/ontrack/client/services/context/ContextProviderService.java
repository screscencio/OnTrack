package br.com.oncast.ontrack.client.services.context;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public interface ContextProviderService {

	public ProjectContext getProjectContext(long projectId);

	public boolean isContextAvailable(long projectId);

	public void loadProjectContext(long requestedProjectId, ProjectContextLoadCallback projectContextLoadCallback);
}