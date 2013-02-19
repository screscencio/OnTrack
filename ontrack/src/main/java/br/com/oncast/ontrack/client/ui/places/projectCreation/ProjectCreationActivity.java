package br.com.oncast.ontrack.client.ui.places.projectCreation;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.alerting.AlertConfirmationListener;
import br.com.oncast.ontrack.client.services.context.ProjectCreationListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectMessagePanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectMessageView;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ProjectCreationActivity extends AbstractActivity {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	private final ProjectCreationMessages messages = GWT.create(ProjectCreationMessages.class);

	private final ProjectCreationPlace projectCreationPlace;

	public ProjectCreationActivity(final ProjectCreationPlace projectCreationPlace) {
		this.projectCreationPlace = projectCreationPlace;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		final ProjectMessageView view = new ProjectMessagePanel();
		panel.setWidget(view);

		final String projectName = projectCreationPlace.getProjectName();
		view.setMainMessage(messages.creatingProject(projectName));
		ClientServiceProvider.getInstance().getProjectRepresentationProvider()
				.createNewProject(projectName, new ProjectCreationListener() {

					@Override
					public void onProjectCreated(final ProjectRepresentation projectRepresentation) {
						openProject(projectRepresentation);
					}

					@Override
					public void onProjectCreationFailure() {
						// TODO +++Treat failure properly
						SERVICE_PROVIDER.getClientAlertingService().showErrorWithConfirmation(
								messages.projectCreationFailed(),
								new AlertConfirmationListener() {
									@Override
									public void onConfirmation() {
										Window.Location.reload();
									}
								});
					}

					@Override
					public void onUnexpectedFailure() {
						// TODO +++Treat failure properly
						SERVICE_PROVIDER.getClientAlertingService().showErrorWithConfirmation(
								messages.itWasNotPossibleToCreateTheProject(),
								new AlertConfirmationListener() {
									@Override
									public void onConfirmation() {
										Window.Location.reload();
									}
								});
					}
				});
		SERVICE_PROVIDER.getClientAlertingService().setAlertingParentWidget(view.asWidget());
	}

	@Override
	public void onStop() {
		SERVICE_PROVIDER.getClientAlertingService().clearAlertingParentWidget();
	}

	protected void openProject(final ProjectRepresentation projectRepresentation) {
		final PlanningPlace projectPlanningPlace = new PlanningPlace(projectRepresentation);
		SERVICE_PROVIDER.getApplicationPlaceController().goTo(projectPlanningPlace);
	}
}
