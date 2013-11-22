package br.com.oncast.ontrack.client.services.context;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;

import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.authentication.UserAuthenticationListener;
import br.com.oncast.ontrack.client.services.metrics.ClientMetricsService;
import br.com.oncast.ontrack.client.services.metrics.TimeTrackingEvent;
import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.metrics.MetricsCategories;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRevision;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextResponse;

import java.util.ArrayList;
import java.util.List;

public class ContextProviderServiceImpl implements ContextProviderService {

	private final ProjectRepresentationProviderImpl projectRepresentationProvider;
	private final DispatchService requestDispatchService;
	private Long loadProjectRevision = null;

	private ProjectContext projectContext;
	private final List<ContextChangeListener> contextLoadListeners;
	private final ClientMetricsService metrics;

	public ContextProviderServiceImpl(final ProjectRepresentationProviderImpl projectRepresentationProvider, final DispatchService requestDispatchService,
			final AuthenticationService authenticationService, final ClientMetricsService metrics) {
		this.metrics = metrics;
		this.contextLoadListeners = new ArrayList<ContextChangeListener>();
		this.projectRepresentationProvider = projectRepresentationProvider;
		this.requestDispatchService = requestDispatchService;

		authenticationService.registerUserAuthenticationListener(new UserAuthenticationListener() {

			@Override
			public void onUserLoggedOut() {
				unloadProjectContext();
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

		return isContextAvailable() && this.projectContext.equals(otherContext);
	}

	private void notifyProjectChange() {
		final UUID currentProjectId = getCurrentProjectId();
		metrics.onProjectChange(currentProjectId);

		for (final ContextChangeListener l : contextLoadListeners) {
			l.onProjectChanged(currentProjectId, loadProjectRevision);
		}
	}

	@Override
	public boolean isContextAvailable(final UUID projectId) {
		return isContextAvailable() && (projectContext.equals(projectId));
	}

	@Override
	public boolean isContextAvailable() {
		return (projectContext != null);
	}

	@Override
	public void loadProjectContext(final UUID requestedProjectId, final ProjectContextLoadCallback projectContextLoadCallback) {
		final TimeTrackingEvent tracking = metrics.startTimeTracking(MetricsCategories.CONTEXT_LOAD, requestedProjectId.toString());
		requestDispatchService.dispatch(new ProjectContextRequest(requestedProjectId), new DispatchCallback<ProjectContextResponse>() {
			@Override
			public void onSuccess(final ProjectContextResponse response) {
				tracking.end();
				final ProjectRevision projectRevision = response.getProjectRevision();
				loadProjectRevision = projectRevision.getRevision();
				setProjectContext(new ProjectContext(projectRevision.getProject()));
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
	public ProjectContext getCurrent() {
		if (!isContextAvailable()) throw new RuntimeException("There is no project context avaliable.");
		return projectContext;
	}

	@Override
	public void addContextLoadListener(final ContextChangeListener contextLoadListener) {
		contextLoadListeners.add(contextLoadListener);

		contextLoadListener.onProjectChanged(getCurrentProjectId(), loadProjectRevision);
	}

	@Override
	public UUID getCurrentProjectId() {
		if (!isContextAvailable()) return null;

		return getCurrent().getProjectRepresentation().getId();
	}

	public interface ContextChangeListener {
		void onProjectChanged(UUID projectId, Long loadedProjectRevision);
	}

	@Override
	public void unloadProjectContext() {
		loadProjectRevision = null;
		setProjectContext(null);
	}

	@Override
	public void revertContext(final ProjectContext context) {
		setProjectContext(context);
	}
}