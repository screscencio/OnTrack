package br.com.oncast.ontrack.mocks;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
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
	public void setProjectContext(final ProjectContext projectContext) {
		this.projectContext = projectContext;
	}

	@Override
	public boolean isContextAvailable(final long projectId) {
		return (projectContext != null);
	}
}
