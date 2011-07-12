package br.com.oncast.ontrack.client.ui.places.contextloading;

import br.com.oncast.ontrack.client.services.communication.CommunicationService;
import br.com.oncast.ontrack.client.services.communication.DispatchCallback;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.services.communication.ProjectContextRequest;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ContextLoadingActivity extends AbstractActivity {

	private final ApplicationPlaceController placeController;
	private final CommunicationService communicationService;
	private final ContextProviderService contextProviderService;
	private final Place place;

	public ContextLoadingActivity(final ContextProviderService contextProviderService, final ApplicationPlaceController placeController,
			final CommunicationService communicationService, final Place destinationPlace) {
		this.contextProviderService = contextProviderService;
		this.placeController = placeController;
		this.communicationService = communicationService;
		this.place = destinationPlace;
	}

	// TODO +Show animations and change the view according to the communication state.
	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		if (contextProviderService.isContextAvailable()) placeController.goTo(place);

		final ContextLoadingView view = new ContextLoadingPanel();
		panel.setWidget(view);

		// TODO Display 'loading' UI indicator.
		communicationService.dispatch(new ProjectContextRequest(), new DispatchCallback<ProjectContext>() {

			@Override
			public void onFailure(final Throwable cause) {
				// TODO Hide 'loading' UI indicator.
				// TODO +++Treat communication failure.
				Window.alert("Error! Could not load project: " + cause.toString());
				cause.printStackTrace();
			}

			@Override
			public void onRequestCompletition(final ProjectContext result) {
				// TODO Hide 'loading' UI indicator.
				contextProviderService.setProjectContext(result);
				placeController.goTo(place);
			}
		});
	}
}