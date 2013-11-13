package br.com.oncast.ontrack.client.ui.places.loading;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.context.ProjectContextLoadCallback;
import br.com.oncast.ontrack.client.services.metrics.TimeTrackingEvent;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectMessagePanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectMessageView;
import br.com.oncast.ontrack.client.ui.places.ProjectDependentPlace;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;
import br.com.oncast.ontrack.shared.metrics.MetricsCategories;
import br.com.oncast.ontrack.shared.metrics.MetricsTokenizer;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ContextLoadingActivity extends AbstractActivity {

	private static final ContextLoadingMessages messages = GWT.create(ContextLoadingMessages.class);

	private static final ClientServices SERVICE_PROVIDER = ClientServices.get();
	private final ProjectDependentPlace projectDependentPlace;

	public ContextLoadingActivity(final ProjectDependentPlace destinationPlace) {
		this.projectDependentPlace = destinationPlace;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		validateGatheredData();
		final TimeTrackingEvent timeTracking = ClientServices.get().metrics().startTimeTracking(MetricsCategories.PLACE_LOAD, MetricsTokenizer.getClassSimpleName(this));

		final ProjectMessageView view = new ProjectMessagePanel();
		panel.setWidget(view);
		view.setMainMessage(messages.syncing());

		SERVICE_PROVIDER.contextProvider().loadProjectContext(projectDependentPlace.getRequestedProjectId(), new ProjectContextLoadCallback() {

			@Override
			public void onProjectContextLoaded() {
				validateGatheredData();
				timeTracking.end();
			}

			@Override
			public void onProjectNotFound() {
				treatFailure(messages.projectNotFound());
			}

			@Override
			public void onUnexpectedFailure(final Throwable cause) {
				cause.printStackTrace();
				treatFailure(cause.getLocalizedMessage());
			}
		});

		SERVICE_PROVIDER.alerting().setAlertingParentWidget(view.getAlertingContainer());
	}

	@Override
	public void onStop() {
		SERVICE_PROVIDER.alerting().clearAlertingParentWidget();
	}

	private void validateGatheredData() {
		if (!SERVICE_PROVIDER.contextProvider().isContextAvailable(projectDependentPlace.getRequestedProjectId())) return;

		SERVICE_PROVIDER.placeController().goTo(projectDependentPlace);
	}

	private void treatFailure(final String message) {
		SERVICE_PROVIDER.alerting().showError(message);
		SERVICE_PROVIDER.placeController().goTo(new ProjectSelectionPlace());
	}

}