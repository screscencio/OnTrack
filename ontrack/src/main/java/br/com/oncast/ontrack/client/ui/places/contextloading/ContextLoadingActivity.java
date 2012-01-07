package br.com.oncast.ontrack.client.ui.places.contextloading;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.context.ProjectContextLoadCallback;
import br.com.oncast.ontrack.client.services.messages.ClientNotificationService;
import br.com.oncast.ontrack.client.services.messages.ClientNotificationService.ConfirmationListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectMessagePanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectMessageView;
import br.com.oncast.ontrack.client.ui.places.ProjectDependentPlace;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ContextLoadingActivity extends AbstractActivity {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();
	private final ProjectDependentPlace projectDependentPlace;

	public ContextLoadingActivity(final ProjectDependentPlace destinationPlace) {
		this.projectDependentPlace = destinationPlace;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		if (SERVICE_PROVIDER.getContextProviderService().isContextAvailable(projectDependentPlace.getRequestedProjectId())) {
			SERVICE_PROVIDER.getApplicationPlaceController().goTo(projectDependentPlace);
		}

		final ProjectMessageView view = new ProjectMessagePanel();
		panel.setWidget(view);

		view.setMainMessage("Syncing...");
		SERVICE_PROVIDER.getContextProviderService().loadProjectContext(projectDependentPlace.getRequestedProjectId(), new ProjectContextLoadCallback() {

			@Override
			public void onProjectContextLoaded() {
				SERVICE_PROVIDER.getApplicationPlaceController().goTo(projectDependentPlace);
			}

			@Override
			public void onProjectNotFound() {
				// TODO +++Treat communication failure.
				ClientNotificationService.showError("Error! Could not load project: The requested project was not found.");
				SERVICE_PROVIDER.getApplicationPlaceController().goTo(new ProjectSelectionPlace());
			}

			@Override
			public void onUnexpectedFailure(final Throwable cause) {
				// TODO +++Treat communication failure.
				cause.printStackTrace();
				ClientNotificationService.showErrorWithConfirmation("Error! Could not load project: " + cause.toString(), new ConfirmationListener() {
					@Override
					public void onConfirmation() {
						Window.Location.reload();
					}
				});
			}
		});
	}
}