package br.com.oncast.ontrack.client.services.context;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;

public interface ContextProviderService {

	public ProjectContext getProjectContext(long projectId);

	public void setProjectContext(final ProjectContext projectContext);

	public boolean isContextAvailable(long projectId);
}