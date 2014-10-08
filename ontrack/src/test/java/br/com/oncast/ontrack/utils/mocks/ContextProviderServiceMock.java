package br.com.oncast.ontrack.utils.mocks;

import br.com.oncast.ontrack.client.services.context.ContextChangeListener;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ProjectContextLoadCallback;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ContextProviderServiceMock implements ContextProviderService {

	ProjectContext projectContext;

	public ContextProviderServiceMock(final ProjectContext projectContext) {
		this.projectContext = projectContext;
	}

	@Override
	public ProjectContext getProjectContext(final UUID projectId) {
		if (isContextAvailable()) return projectContext;
		throw new RuntimeException();
	}

	@Override
	public boolean isContextAvailable(final UUID projectId) {
		return isContextAvailable() && projectContext.equals(projectId);
	}

	@Override
	public void loadProjectContext(final UUID requestedProjectId, final ProjectContextLoadCallback projectContextLoadCallback) {
		throw new RuntimeException("Should not be called.");
	}

	@Override
	public ProjectContext getCurrent() {
		if (isContextAvailable()) return projectContext;
		throw new RuntimeException();
	}

	@Override
	public void addContextLoadListener(final ContextChangeListener contextLoadListener) {}

	@Override
	public void unloadProjectContext() {}

	@Override
	public void revertContext(final ProjectContext context) {}

	@Override
	public boolean isContextAvailable() {
		return projectContext != null;
	}

	@Override
	public UUID getCurrentProjectId() {
		return null;
	}
}
