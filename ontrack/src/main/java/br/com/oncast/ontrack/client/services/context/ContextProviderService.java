package br.com.oncast.ontrack.client.services.context;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public interface ContextProviderService {

	public ProjectContext getProjectContext();

	public void setProjectContext(final ProjectContext projectContext);

	public boolean isContextAvailable();
}