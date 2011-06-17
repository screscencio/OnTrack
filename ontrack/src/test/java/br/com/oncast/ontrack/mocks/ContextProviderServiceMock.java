package br.com.oncast.ontrack.mocks;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.shared.project.ProjectContext;

public class ContextProviderServiceMock implements ContextProviderService {

	ProjectContext projectContext;

	public ContextProviderServiceMock(final ProjectContext projectContext) {
		this.projectContext = projectContext;
	}

	@Override
	public ProjectContext getProjectContext() {
		if (projectContext != null) return projectContext;
		throw new RuntimeException();
	}

	@Override
	public void setProjectContext(final ProjectContext projectContext) {
		this.projectContext = projectContext;
	}

	@Override
	public boolean isContextAvailable() {
		return (projectContext != null);
	}
}
