package br.com.oncast.ontrack.client.ui.places.loading;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.authentication.UserInformationLoadCallback;
import br.com.oncast.ontrack.client.services.notification.NotificationConfirmationListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectMessagePanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectMessageView;
import br.com.oncast.ontrack.shared.model.user.User;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class UserInformationLoadingActivity extends AbstractActivity {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();
	private final Place destinationPlace;

	public UserInformationLoadingActivity(final Place destinationPlace) {
		ClientServiceProvider.getInstance().getClientMetricService().onBrowserLoadStart();
		this.destinationPlace = destinationPlace;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		validateGatheredData();

		final ProjectMessageView view = new ProjectMessagePanel();
		panel.setWidget(view);
		view.setMainMessage("Loading user data");

		SERVICE_PROVIDER.getAuthenticationService().loadCurrentUserInformation(new UserInformationLoadCallback() {

			@Override
			public void onUserInformationLoaded(final User currentUser) {
				validateGatheredData();
			}

			@Override
			public void onUnexpectedFailure(final Throwable cause) {
				treatUnexpectedFailure(cause, "Could not load user information: " + cause.getMessage());
			}

		});
		SERVICE_PROVIDER.getClientNotificationService().setNotificationParentWidget(view.asWidget());
		ClientServiceProvider.getInstance().getClientMetricService().onBrowserLoadEnd();
	}

	@Override
	public void onStop() {
		SERVICE_PROVIDER.getClientNotificationService().clearNotificationParentWidget();
	}

	private void validateGatheredData() {
		if (!SERVICE_PROVIDER.getAuthenticationService().isUserAvailable()) return;
		SERVICE_PROVIDER.getApplicationPlaceController().goTo(destinationPlace);
	}

	private void treatUnexpectedFailure(final Throwable cause, final String message) {
		// TODO +++Treat communication failure.
		cause.printStackTrace();
		SERVICE_PROVIDER.getClientNotificationService().showErrorWithConfirmation(message, new NotificationConfirmationListener() {
			@Override
			public void onConfirmation() {
				Window.Location.reload();
			}
		});
	}

}