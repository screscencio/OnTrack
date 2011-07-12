package br.com.oncast.ontrack.client.services.context;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public class ContextProviderServiceImpl implements ContextProviderService {

	private ProjectContext projectContext;

	@Override
	public ProjectContext getProjectContext() {
		if (projectContext != null) return projectContext;
		throw new RuntimeException();
	}

	// TODO +Verify: Throw an event on the eventBus when the context is set so that UI and other services can update theirselfs.
	@Override
	public void setProjectContext(final ProjectContext projectContext) {
		this.projectContext = projectContext;
	}

	@Override
	public boolean isContextAvailable() {
		return (projectContext != null);
	}
}