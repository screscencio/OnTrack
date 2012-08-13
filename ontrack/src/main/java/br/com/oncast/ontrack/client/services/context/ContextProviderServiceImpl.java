package br.com.oncast.ontrack.client.services.context;

import java.util.ArrayList;
import java.util.List;

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
	private final List<ContextChangeListener> contextLoadListeners;

	public ContextProviderServiceImpl(final ProjectRepresentationProviderImpl projectRepresentationProvider,
			final DispatchService requestDispatchService,
			final AuthenticationService authenticationService) {

		this.contextLoadListeners = new ArrayList<ContextChangeListener>();
		this.projectRepresentationProvider = projectRepresentationProvider;
		this.requestDispatchService = requestDispatchService;

		authenticationService.registerUserAuthenticationListener(new UserAuthenticationListener() {

			@Override
			public void onUserLoggedOut() {
				setProjectContext(null);
			}

			@Override
			public void onUserLoggedIn() {}

			@Override
			public void onUserInformationLoaded() {}

		});
	}

	@Override
	public ProjectContext getProjectContext(final UUID projectId) {
		if (isContextAvailable(projectId)) return projectContext;
		throw new RuntimeException("There is no project context avaliable.");
	}

	private void setProjectContext(final ProjectContext projectContext) {
		final boolean wasSameContext = sameContext(projectContext);
		this.projectContext = projectContext;

		if (wasSameContext) return;

		projectRepresentationProvider.setProjectRepresentation(projectContext == null ? null : projectContext.getProjectRepresentation());
		notifyProjectChange();
	}

	private boolean sameContext(final ProjectContext otherContext) {
		if (this.projectContext == otherContext) return true;

		return this.projectContext != null && this.projectContext.equals(otherContext);
	}

	private void notifyProjectChange() {
		final UUID currentProjectId = getCurrentProjectId();

		for (final ContextChangeListener l : contextLoadListeners) {
			l.onProjectChanged(currentProjectId);
		}
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

	@Override
	public void addContextLoadListener(final ContextChangeListener contextLoadListener) {
		contextLoadListeners.add(contextLoadListener);

		contextLoadListener.onProjectChanged(getCurrentProjectId());
	}

	private UUID getCurrentProjectId() {
		final ProjectContext currentProjectContext = getCurrentProjectContext();
		return currentProjectContext == null ? null : currentProjectContext.getProjectRepresentation().getId();
	}

	public interface ContextChangeListener {
		void onProjectChanged(UUID projetId);
	}
}