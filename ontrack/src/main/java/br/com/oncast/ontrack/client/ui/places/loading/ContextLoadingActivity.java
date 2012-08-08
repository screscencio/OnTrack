package br.com.oncast.ontrack.client.ui.places.loading;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.context.ProjectContextLoadCallback;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectMessagePanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectMessageView;
import br.com.oncast.ontrack.client.ui.places.ProjectDependentPlace;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ContextLoadingActivity extends AbstractActivity {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();
	private final ProjectDependentPlace projectDependentPlace;

	public ContextLoadingActivity(final ProjectDependentPlace destinationPlace) {
		this.projectDependentPlace = destinationPlace;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		validateGatheredData();

		final ProjectMessageView view = new ProjectMessagePanel();
		panel.setWidget(view);
		view.setMainMessage("Syncing...");

		SERVICE_PROVIDER.getAnnotationService().loadAnnotatedSubjectIds(projectDependentPlace.getRequestedProjectId());
		SERVICE_PROVIDER.getContextProviderService().loadProjectContext(projectDependentPlace.getRequestedProjectId(), new ProjectContextLoadCallback() {

			@Override
			public void onProjectContextLoaded() {
				validateGatheredData();
			}

			@Override
			public void onProjectNotFound() {
				treatFailure("The requested project was not found.");
			}

			@Override
			public void onUnexpectedFailure(final Throwable cause) {
				cause.printStackTrace();
				treatFailure(cause.getMessage());
			}
		});

		SERVICE_PROVIDER.getClientNotificationService().setNotificationParentWidget(view.asWidget());
	}

	@Override
	public void onStop() {
		SERVICE_PROVIDER.getClientNotificationService().clearNotificationParentWidget();
	}

	private void validateGatheredData() {
		if (!SERVICE_PROVIDER.getContextProviderService().isContextAvailable(projectDependentPlace.getRequestedProjectId())) return;
		SERVICE_PROVIDER.getApplicationPlaceController().goTo(projectDependentPlace);
	}

	private void treatFailure(final String message) {
		SERVICE_PROVIDER.getClientNotificationService().showError(message);
		SERVICE_PROVIDER.getApplicationPlaceController().goTo(new ProjectSelectionPlace());
	}

}