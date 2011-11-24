package br.com.oncast.ontrack.utils.mocks;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ProjectContextLoadCallback;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public class ContextProviderServiceMock implements ContextProviderService {

	ProjectContext projectContext;

	public ContextProviderServiceMock(final ProjectContext projectContext) {
		this.projectContext = projectContext;
	}

	@Override
	public ProjectContext getProjectContext(final long projectId) {
		if (projectContext != null) return projectContext;
		throw new RuntimeException();
	}

	@Override
	public boolean isContextAvailable(final long projectId) {
		return (projectContext != null);
	}

	@Override
	public void loadProjectContext(final long requestedProjectId, final ProjectContextLoadCallback projectContextLoadCallback) {
		throw new RuntimeException("Should not be called.");
	}
}
