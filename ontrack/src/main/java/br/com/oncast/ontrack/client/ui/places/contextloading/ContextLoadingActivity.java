package br.com.oncast.ontrack.client.ui.places.contextloading;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.context.ProjectContextLoadCallback;
import br.com.oncast.ontrack.client.ui.places.ProjectDependentPlace;

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

	// TODO +Show animations and change the view according to the communication state.
	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		if (SERVICE_PROVIDER.getContextProviderService().isContextAvailable(projectDependentPlace.getRequestedProjectId())) {
			SERVICE_PROVIDER.getApplicationPlaceController().goTo(projectDependentPlace);
		}

		final ContextLoadingView view = new ContextLoadingPanel();
		panel.setWidget(view);

		// TODO Display 'loading' UI indicator.
		SERVICE_PROVIDER.getContextProviderService().loadProjectContext(projectDependentPlace.getRequestedProjectId(), new ProjectContextLoadCallback() {

			@Override
			public void onProjectContextLoaded() {
				// TODO Hide 'loading' UI indicator.
				SERVICE_PROVIDER.getApplicationPlaceController().goTo(projectDependentPlace);
			}

			@Override
			public void onProjectNotFound() {
				// TODO Hide 'loading' UI indicator.
				// FIXME Treat ProjectNotFoundException
				Window.alert("Error! Could not load project: The requested project was not found.");
			}

			@Override
			public void onUnexpectedFailure(final Throwable cause) {
				// TODO Hide 'loading' UI indicator.
				// TODO +++Treat communication failure.
				// FIXME Call the error treatment exception.
				Window.alert("Error! Could not load project: " + cause.toString());
				cause.printStackTrace();
			}
		});
	}
}