package br.com.oncast.ontrack.client.services.context;

import br.com.oncast.ontrack.client.services.identification.ClientIdentificationProvider;
import br.com.oncast.ontrack.client.services.requestDispatch.DispatchCallback;
import br.com.oncast.ontrack.client.services.requestDispatch.RequestDispatchService;
import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;

public class ContextProviderServiceImpl implements ContextProviderService {

	private final ProjectRepresentationProviderImpl projectRepresentationProvider;
	private final ClientIdentificationProvider clientIdentificationProvider;
	private final RequestDispatchService requestDispatchService;

	private ProjectContext projectContext;

	public ContextProviderServiceImpl(final ProjectRepresentationProviderImpl projectRepresentationProvider,
			final ClientIdentificationProvider clientIdentificationProvider, final RequestDispatchService requestDispatchService) {
		this.projectRepresentationProvider = projectRepresentationProvider;
		this.clientIdentificationProvider = clientIdentificationProvider;
		this.requestDispatchService = requestDispatchService;
	}

	@Override
	public ProjectContext getProjectContext(final long projectId) {
		if (isContextAvailable(projectId)) return projectContext;
		throw new RuntimeException("There is no project context avaliable.");
	}

	private void setProjectContext(final ProjectContext projectContext) {
		this.projectContext = projectContext;
		projectRepresentationProvider.setProjectRepresentation(projectContext.getProjectRepresentation());
	}

	@Override
	public boolean isContextAvailable(final long projectId) {
		return (projectContext != null) && (projectContext.getProjectRepresentation().getId() == projectId);
	}

	@Override
	public void loadProjectContext(final long requestedProjectId, final ProjectContextLoadCallback projectContextLoadCallback) {
		requestDispatchService.dispatch(new ProjectContextRequest(clientIdentificationProvider.getClientId(), requestedProjectId),
				new DispatchCallback<ProjectContext>() {

					@Override
					public void onRequestCompletition(final ProjectContext projectContext) {
						setProjectContext(projectContext);
						projectContextLoadCallback.onProjectContextLoaded();
					}

					@Override
					public void onFailure(final Throwable cause) {
						// TODO +++Treat communication failure.
						if (cause instanceof ProjectNotFoundException) projectContextLoadCallback.onProjectNotFound();
						else projectContextLoadCallback.onUnexpectedFailure(cause);
					}
				});
	}
}