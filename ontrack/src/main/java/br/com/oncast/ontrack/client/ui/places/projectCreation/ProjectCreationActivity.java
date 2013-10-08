package br.com.oncast.ontrack.client.ui.places.projectCreation;

import br.com.oncast.ontrack.client.services.ClientServices;
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

	private static final ClientServices SERVICE_PROVIDER = ClientServices.get();

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
		ClientServices.get().projectRepresentationProvider().createNewProject(projectName, new ProjectCreationListener() {

			@Override
			public void onProjectCreated(final ProjectRepresentation projectRepresentation) {
				openProject(projectRepresentation);
			}

			@Override
			public void onProjectCreationFailure() {
				// TODO +++Treat failure properly
				SERVICE_PROVIDER.alerting().showErrorWithConfirmation(messages.projectCreationFailed(), new AlertConfirmationListener() {
					@Override
					public void onConfirmation() {
						Window.Location.reload();
					}
				});
			}

			@Override
			public void onUnexpectedFailure() {
				// TODO +++Treat failure properly
				SERVICE_PROVIDER.alerting().showErrorWithConfirmation(messages.itWasNotPossibleToCreateTheProject(), new AlertConfirmationListener() {
					@Override
					public void onConfirmation() {
						Window.Location.reload();
					}
				});
			}
		});
		SERVICE_PROVIDER.alerting().setAlertingParentWidget(view.getAlertingContainer());
	}

	@Override
	public void onStop() {
		SERVICE_PROVIDER.alerting().clearAlertingParentWidget();
	}

	protected void openProject(final ProjectRepresentation projectRepresentation) {
		final PlanningPlace projectPlanningPlace = new PlanningPlace(projectRepresentation);
		SERVICE_PROVIDER.placeController().goTo(projectPlanningPlace);
	}
}
