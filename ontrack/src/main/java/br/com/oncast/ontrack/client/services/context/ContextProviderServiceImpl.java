package br.com.oncast.ontrack.client.services.context;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.authentication.UserAuthenticationListener;
import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextResponse;

public class ContextProviderServiceImpl implements ContextProviderService {

	private final ProjectRepresentationProviderImpl projectRepresentationProvider;
	private final DispatchService requestDispatchService;

	private ProjectContext projectContext;

	public ContextProviderServiceImpl(final ProjectRepresentationProviderImpl projectRepresentationProvider,
			final DispatchService requestDispatchService,
			final AuthenticationService authenticationService) {

		this.projectRepresentationProvider = projectRepresentationProvider;
		this.requestDispatchService = requestDispatchService;

		authenticationService.registerUserAuthenticationListener(new UserAuthenticationListener() {

			@Override
			public void onUserLoggedOut() {
				setProjectContext(null);
			}

			@Override
			public void onUserLoggedIn() {}
		});
	}

	@Override
	public ProjectContext getProjectContext(final UUID projectId) {
		if (isContextAvailable(projectId)) return projectContext;
		throw new RuntimeException("There is no project context avaliable.");
	}

	private void setProjectContext(final ProjectContext projectContext) {
		this.projectContext = projectContext;
		projectRepresentationProvider.setProjectRepresentation(projectContext == null ? null : projectContext.getProjectRepresentation());
	}

	@Override
	public boolean isContextAvailable(final UUID projectId) {
		return (projectContext != null) && (projectContext.getProjectRepresentation().getId().equals(projectId));
	}

	@Override
	public void loadProjectContext(final UUID requestedProjectId, final ProjectContextLoadCallback projectContextLoadCallback) {
		requestDispatchService.dispatch(new ProjectContextRequest(requestedProjectId),
				new DispatchCallback<ProjectContextResponse>() {

					@Override
					public void onSuccess(final ProjectContextResponse response) {
						setProjectContext(new ProjectContext(response.getProject()));
						projectContextLoadCallback.onProjectContextLoaded();
					}

					@Override
					public void onTreatedFailure(final Throwable caught) {}

					@Override
					public void onUntreatedFailure(final Throwable caught) {
						// TODO +++Treat communication failure.
						if (caught instanceof ProjectNotFoundException) projectContextLoadCallback.onProjectNotFound();
						else projectContextLoadCallback.onUnexpectedFailure(caught);
					}
				});
	}

	@Override
	public ProjectContext getCurrentProjectContext() {
		if (projectContext == null) throw new RuntimeException("There is no project context avaliable.");
		return projectContext;
	}
}