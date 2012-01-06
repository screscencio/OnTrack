package br.com.oncast.ontrack.client.ui.places.projectCreation;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.context.ProjectCreationListener;
import br.com.oncast.ontrack.client.services.messages.ClientNotificationService;
import br.com.oncast.ontrack.client.services.messages.ClientNotificationService.ConfirmationListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectMessagePanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectMessageView;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ProjectCreationActivity extends AbstractActivity {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	private final ProjectCreationPlace projectCreationPlace;

	public ProjectCreationActivity(final ProjectCreationPlace projectCreationPlace) {
		this.projectCreationPlace = projectCreationPlace;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final ProjectMessageView view = new ProjectMessagePanel();
		panel.setWidget(view);

		view.setMainMessage("Creating project '" + projectCreationPlace.getProjectName() + "'");
		ClientServiceProvider.getInstance().getProjectRepresentationProvider()
				.createNewProject(projectCreationPlace.getProjectName(), new ProjectCreationListener() {

					@Override
					public void onProjectCreated(final ProjectRepresentation projectRepresentation) {
						openProject(projectRepresentation);
					}

					@Override
					public void onProjectCreationFailure() {
						// TODO +++Treat failure properly
						ClientNotificationService.showErrorWithConfirmation("The server could not create the project. An error occurred.", new ConfirmationListener() {
							@Override
							public void onConfirmation() {
								Window.Location.reload();
							}
						});
					}

					@Override
					public void onUnexpectedFailure() {
						// TODO +++Treat failure properly
						ClientNotificationService.showErrorWithConfirmation("It was not possible to create the project.\n Verify your connection status.",
								new ConfirmationListener() {
									@Override
									public void onConfirmation() {
										Window.Location.reload();
									}
								});
					}
				});
	}

	protected void openProject(final ProjectRepresentation projectRepresentation) {
		final PlanningPlace projectPlanningPlace = new PlanningPlace(projectRepresentation);
		SERVICE_PROVIDER.getApplicationPlaceController().goTo(projectPlanningPlace);
	}
}
