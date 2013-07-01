package br.com.oncast.ontrack.client.services.context;

import java.util.HashSet;
import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.authentication.UserAuthenticationListener;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.ui.settings.DefaultViewSettings;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateProjectRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.context.ProjectAddedEvent;
import br.com.oncast.ontrack.shared.services.context.ProjectAddedEventHandler;
import br.com.oncast.ontrack.shared.services.context.ProjectRemovedEvent;
import br.com.oncast.ontrack.shared.services.context.ProjectRemovedEventHandler;
import br.com.oncast.ontrack.shared.services.context.ProjectRenamedEvent;
import br.com.oncast.ontrack.shared.services.context.ProjectRenamedEventHandler;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectAuthorizationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectAuthorizationResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.RemoveProjectAuthorizationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.RemoveProjectAuthorizationResponse;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;

public class ProjectRepresentationProviderImpl implements ProjectRepresentationProvider {

	private final DispatchService dispatchService;
	private final ClientAlertingService alertingService;
	private final Set<ProjectListChangeListener> projectListChangeListeners = new HashSet<ProjectListChangeListener>();
	private final Set<ProjectRepresentation> availableProjectRepresentations = new HashSet<ProjectRepresentation>();
	private boolean projectListAvailability;
	private ProjectRepresentation currentProjectRepresentation;
	private ClientErrorMessages messages;

	public ProjectRepresentationProviderImpl(final DispatchService dispatchService, final ServerPushClientService serverPushClientService, final AuthenticationService authenticationService,
			final ClientAlertingService alertingService, final ClientErrorMessages messages) {

		this.dispatchService = dispatchService;
		this.alertingService = alertingService;
		this.messages = messages;

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

			@Override
			public void onUserInformationLoaded() {
				updateAvailableProjectRepresentations();
			}
		});

		serverPushClientService.registerServerEventHandler(ProjectAddedEvent.class, new ProjectAddedEventHandler() {
			@Override
			public void onEvent(final ProjectAddedEvent event) {
				final ProjectRepresentation projectRepresentation = event.getProjectRepresentation();
				if (availableProjectRepresentations.contains(projectRepresentation)) return;
				availableProjectRepresentations.add(projectRepresentation);
				notifyProjectListContentChange();
			}
		});

		serverPushClientService.registerServerEventHandler(ProjectRemovedEvent.class, new ProjectRemovedEventHandler() {
			@Override
			public void onEvent(final ProjectRemovedEvent event) {
				final ProjectRepresentation projectRepresentation = event.getProjectRepresentation();
				if (!availableProjectRepresentations.contains(projectRepresentation)) return;
				availableProjectRepresentations.remove(projectRepresentation);
				alertingService.showWarning(messages.authorizationRevogued(projectRepresentation.getName()));
				notifyProjectListContentChange();
			}
		});

		serverPushClientService.registerServerEventHandler(ProjectRenamedEvent.class, new ProjectRenamedEventHandler() {
			@Override
			public void onEvent(final ProjectRenamedEvent event) {
				final ProjectRepresentation projectRepresentation = event.getProjectRepresentation();
				if (!availableProjectRepresentations.contains(projectRepresentation)) return;
				availableProjectRepresentations.remove(projectRepresentation);
				availableProjectRepresentations.add(projectRepresentation);
				notifyProjectListContentChange();
				notifyProjectNameUpdated(projectRepresentation);
				if (currentProjectRepresentation.equals(projectRepresentation)) setProjectRepresentation(projectRepresentation);
			}

		});

		if (authenticationService.isUserAvailable()) updateAvailableProjectRepresentations();
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
				// TODO +++Treat fatal error. Could not load project list...
				alertingService.showWarning(messages.projectListUnavailable());
			}

		});
	}

	private void notifyProjectNameUpdated(final ProjectRepresentation projectRepresentation) {
		for (final ProjectListChangeListener listener : projectListChangeListeners) {
			listener.onProjectNameUpdate(projectRepresentation);
		}
	}

	@Override
	public ProjectRepresentation getCurrent() {
		if (!hasAvailableProjectRepresentation()) throw new RuntimeException("There is no project representation set.");
		return currentProjectRepresentation;
	}

	protected void setProjectRepresentation(final ProjectRepresentation projectRepresentation) {
		this.currentProjectRepresentation = projectRepresentation;
		Window.setTitle(projectRepresentation == null ? DefaultViewSettings.TITLE : currentProjectRepresentation.getName());
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
	public void authorizeUser(final String mail, final boolean superUser, final ProjectAuthorizationCallback callback) {
		dispatchService.dispatch(new ProjectAuthorizationRequest(currentProjectRepresentation.getId(), mail, superUser), new DispatchCallback<ProjectAuthorizationResponse>() {
			@Override
			public void onSuccess(final ProjectAuthorizationResponse result) {
				callback.onSuccess();
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

	@Override
	public HandlerRegistration registerProjectListChangeListener(final ProjectListChangeListener projectListChangeListener) {
		if (projectListChangeListeners.add(projectListChangeListener)) notifyProjectListChange(projectListChangeListener);

		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				projectListChangeListeners.remove(projectListChangeListener);
			}
		};
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

	@Override
	public ProjectRepresentation getProjectRepresentation(final UUID projectId) {
		for (final ProjectRepresentation projectRepresentation : availableProjectRepresentations) {
			if (projectRepresentation.getId().equals(projectId)) return projectRepresentation;
		}
		throw new RuntimeException("Project representation not available.");
	}

	@Override
	public boolean hasAvailableProjectRepresentation() {
		return currentProjectRepresentation != null;
	}

	@Override
	public void unauthorizeUser(final UserRepresentation user, final ProjectAuthorizationCallback callback) {
		dispatchService.dispatch(new RemoveProjectAuthorizationRequest(currentProjectRepresentation.getId(), user.getId()), new DispatchCallback<RemoveProjectAuthorizationResponse>() {
			@Override
			public void onSuccess(final RemoveProjectAuthorizationResponse result) {
				callback.onSuccess();
			}

			@Override
			public void onTreatedFailure(final Throwable caught) {}

			@Override
			public void onUntreatedFailure(final Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

}
