package br.com.oncast.ontrack.client.ui.places.loading;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.alerting.AlertConfirmationListener;
import br.com.oncast.ontrack.client.services.authentication.UserInformationLoadCallback;
import br.com.oncast.ontrack.client.services.metrics.TimeTrackingEvent;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectMessagePanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectMessageView;
import br.com.oncast.ontrack.shared.metrics.MetricsCategories;
import br.com.oncast.ontrack.shared.metrics.MetricsTokenizer;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class UserInformationLoadingActivity extends AbstractActivity {

	private static final ClientServices SERVICE_PROVIDER = ClientServices.get();

	private static UserInformationLoadingMessages messages = GWT.create(UserInformationLoadingMessages.class);

	private final Place destinationPlace;

	public UserInformationLoadingActivity(final Place destinationPlace) {
		this.destinationPlace = destinationPlace;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		validateGatheredData();
		final TimeTrackingEvent trackingEvent = ClientServices.get().metrics().startTimeTracking(MetricsCategories.PLACE_LOAD, MetricsTokenizer.getClassSimpleName(this));

		final ProjectMessageView view = new ProjectMessagePanel();
		panel.setWidget(view);
		view.setMainMessage(messages.loadingUserData());

		SERVICE_PROVIDER.authentication().loadCurrentUserInformation(new UserInformationLoadCallback() {

			@Override
			public void onUserInformationLoaded(final UUID userId) {
				validateGatheredData();
				trackingEvent.end();
			}

			@Override
			public void onUnexpectedFailure(final Throwable cause) {
				treatUnexpectedFailure(cause, messages.couldNotLoadUserInformation());
			}

		});
		SERVICE_PROVIDER.alerting().setAlertingParentWidget(view.getAlertingContainer());
	}

	@Override
	public void onStop() {
		SERVICE_PROVIDER.alerting().clearAlertingParentWidget();
	}

	private void validateGatheredData() {
		if (!SERVICE_PROVIDER.authentication().isUserAvailable()) return;
		SERVICE_PROVIDER.placeController().goTo(destinationPlace);
	}

	private void treatUnexpectedFailure(final Throwable cause, final String message) {
		// TODO +++Treat communication failure.
		cause.printStackTrace();
		SERVICE_PROVIDER.alerting().showErrorWithConfirmation(message, new AlertConfirmationListener() {
			@Override
			public void onConfirmation() {
				Window.Location.reload();
			}
		});
	}

}