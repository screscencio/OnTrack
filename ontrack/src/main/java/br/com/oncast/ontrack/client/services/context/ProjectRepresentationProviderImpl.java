package br.com.oncast.ontrack.client.services.context;

import java.util.HashSet;
import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateProjectRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.context.NewProjectCreatedEventHandler;
import br.com.oncast.ontrack.shared.services.context.ProjectCreatedEvent;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListResponse;

import com.google.gwt.user.client.Window;

// FIXME Listen for auth changes, clean on logout and reload on login.
public class ProjectRepresentationProviderImpl implements ProjectRepresentationProvider {

	private final DispatchService dispatchService;
	private final Set<ProjectListChangeListener> projectListChangeListeners = new HashSet<ProjectListChangeListener>();
	private final Set<ProjectRepresentation> availableProjectRepresentations = new HashSet<ProjectRepresentation>();
	private ProjectRepresentation currentProjectRepresentation;

	public ProjectRepresentationProviderImpl(final DispatchService dispatchService, final ServerPushClientService serverPushClientService) {
		this.dispatchService = dispatchService;

		serverPushClientService.registerServerEventHandler(ProjectCreatedEvent.class, new NewProjectCreatedEventHandler() {

			@Override
			public void onEvent(final ProjectCreatedEvent event) {
				final ProjectRepresentation newProjectRepresentation = event.getProjectRepresentation();
				if (availableProjectRepresentations.contains(newProjectRepresentation)) return;
				availableProjectRepresentations.add(newProjectRepresentation);
				notifyListenersForCurrentProjectListChange();
			}
		});

		dispatchService.dispatch(new ProjectListRequest(), new DispatchCallback<ProjectListResponse>() {

			@Override
			public void onSuccess(final ProjectListResponse response) {
				availableProjectRepresentations.addAll(response.getProjectList());
				notifyListenersForCurrentProjectListChange();
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				// TODO +++Treat fatal error. COuld not load project list...
				Window.alert("It was not possible to load the project list.\n Verify your internet connection and try again later.");
			}
		});
	}

	@Override
	public ProjectRepresentation getCurrentProjectRepresentation() {
		if (currentProjectRepresentation == null) throw new RuntimeException("There is no project representation set yet.");
		return currentProjectRepresentation;
	}

	protected void setProjectRepresentation(final ProjectRepresentation projectRepresentation) {
		this.currentProjectRepresentation = projectRepresentation;
	}

	@Override
	public void createNewProject(final String projectName, final ProjectCreationListener projectCreationListener) {
		dispatchService.dispatch(new ProjectCreationRequest(projectName), new DispatchCallback<ProjectCreationResponse>() {

			@Override
			public void onSuccess(final ProjectCreationResponse response) {
				projectCreationListener.onProjectCreated(response.getProjectRepresentation());
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				if (caught instanceof UnableToCreateProjectRepresentation) projectCreationListener.onProjectCreationFailure();
				else projectCreationListener.onUnexpectedFailure();
			}
		});
	}

	@Override
	public void registerProjectListChangeListener(final ProjectListChangeListener projectListChangeListener) {
		if (projectListChangeListeners.contains(projectListChangeListener)) return;
		projectListChangeListeners.add(projectListChangeListener);
		notifyListenerForCurrentProjectListChange(projectListChangeListener);
	}

	@Override
	public void unregisterProjectListChangeListener(final ProjectListChangeListener projectListChangeListener) {
		projectListChangeListeners.remove(projectListChangeListener);
	}

	private void notifyListenersForCurrentProjectListChange() {
		for (final ProjectListChangeListener listener : projectListChangeListeners)
			notifyListenerForCurrentProjectListChange(listener);
	}

	private void notifyListenerForCurrentProjectListChange(final ProjectListChangeListener projectListChangeListener) {
		projectListChangeListener.onProjectListChanged(availableProjectRepresentations);
	}
}
