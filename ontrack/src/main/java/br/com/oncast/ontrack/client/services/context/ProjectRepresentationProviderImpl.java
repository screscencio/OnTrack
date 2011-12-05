package br.com.oncast.ontrack.client.services.context;

import java.util.HashSet;
import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.authentication.UserAuthenticationListener;
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

public class ProjectRepresentationProviderImpl implements ProjectRepresentationProvider {

	private final DispatchService dispatchService;
	private final Set<ProjectListChangeListener> projectListChangeListeners = new HashSet<ProjectListChangeListener>();
	private final Set<ProjectRepresentation> availableProjectRepresentations = new HashSet<ProjectRepresentation>();
	private boolean projectListAvailability;
	private ProjectRepresentation currentProjectRepresentation;

	public ProjectRepresentationProviderImpl(final DispatchService dispatchService, final ServerPushClientService serverPushClientService,
			final AuthenticationService authenticationService) {

		this.dispatchService = dispatchService;

		authenticationService.registerUserAuthenticationListener(new UserAuthenticationListener() {
			@Override
			public void onUserLoggedIn() {
				updateAvailableProjectRepresentations();
			}

			@Override
			public void onUserLoggedOut() {
				availableProjectRepresentations.clear();
				projectListAvailability = false;

				notifyProjectListChange();
			}
		});

		serverPushClientService.registerServerEventHandler(ProjectCreatedEvent.class, new NewProjectCreatedEventHandler() {

			@Override
			public void onEvent(final ProjectCreatedEvent event) {
				final ProjectRepresentation newProjectRepresentation = event.getProjectRepresentation();
				if (availableProjectRepresentations.contains(newProjectRepresentation)) return;
				availableProjectRepresentations.add(newProjectRepresentation);
				notifyProjectListContentChange();
			}
		});

		updateAvailableProjectRepresentations();
	}

	private void updateAvailableProjectRepresentations() {
		dispatchService.dispatch(new ProjectListRequest(), new DispatchCallback<ProjectListResponse>() {

			@Override
			public void onSuccess(final ProjectListResponse response) {
				availableProjectRepresentations.clear();
				availableProjectRepresentations.addAll(response.getProjectList());
				projectListAvailability = true;

				notifyProjectListChange();
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				// TODO +++Treat fatal error. COuld not load project list...
				Window.alert("It was not possible to load the project list.\n Verify your internet connection and reload the application.");
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
		notifyProjectListChange(projectListChangeListener);
	}

	@Override
	public void unregisterProjectListChangeListener(final ProjectListChangeListener projectListChangeListener) {
		projectListChangeListeners.remove(projectListChangeListener);
	}

	private void notifyProjectListChange() {
		for (final ProjectListChangeListener listener : projectListChangeListeners) {
			notifyProjectListContentChange(listener);
			notifyProjectListAvailabilityChange(listener);
		}
	}

	private void notifyProjectListChange(final ProjectListChangeListener listener) {
		notifyProjectListContentChange(listener);
		notifyProjectListAvailabilityChange(listener);
	}

	private void notifyProjectListContentChange() {
		for (final ProjectListChangeListener listener : projectListChangeListeners)
			notifyProjectListContentChange(listener);
	}

	private void notifyProjectListContentChange(final ProjectListChangeListener projectListChangeListener) {
		projectListChangeListener.onProjectListChanged(availableProjectRepresentations);
	}

	protected void notifyProjectListAvailabilityChange(final ProjectListChangeListener projectListChangeListener) {
		projectListChangeListener.onProjectListAvailabilityChange(projectListAvailability);
	}
}
