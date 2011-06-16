package br.com.oncast.ontrack.client.services.context;

import br.com.oncast.ontrack.shared.project.ProjectContext;

public class ContextProviderService {

	ProjectContext projectContext;

	// TODO Study the possibility of directing the application to the loadingPlace instead of throwing this exception.
	public ProjectContext getProjectContext() {
		if (projectContext != null) return projectContext;
		throw new RuntimeException();
	}

	// TODO Throw an event on the eventBus when the context is set so that UI and other services can update theirselfs.
	public void setProjectContext(final ProjectContext projectContext) {
		this.projectContext = projectContext;
	}
}