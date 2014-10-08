package br.com.oncast.ontrack.client.services.context;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ContextProviderService {

	ProjectContext getProjectContext(UUID projectId);

	boolean isContextAvailable();

	boolean isContextAvailable(UUID projectId);

	void loadProjectContext(UUID requestedProjectId, ProjectContextLoadCallback projectContextLoadCallback);

	ProjectContext getCurrent();

	void addContextLoadListener(ContextChangeListener contextLoadListener);

	void unloadProjectContext();

	void revertContext(ProjectContext context);

	UUID getCurrentProjectId();

}