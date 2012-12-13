package br.com.oncast.ontrack.client.services.context;

import br.com.oncast.ontrack.client.services.context.ContextProviderServiceImpl.ContextChangeListener;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ContextProviderService {

	public ProjectContext getProjectContext(UUID projectId);

	public boolean isContextAvailable(UUID projectId);

	public void loadProjectContext(UUID requestedProjectId, ProjectContextLoadCallback projectContextLoadCallback);

	public ProjectContext getCurrent();

	public void addContextLoadListener(ContextChangeListener contextLoadListener);

	public void unloadProjectContext();
}