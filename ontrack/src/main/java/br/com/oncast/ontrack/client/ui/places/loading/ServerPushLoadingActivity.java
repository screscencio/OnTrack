package br.com.oncast.ontrack.client.ui.places.loading;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.notification.NotificationConfirmationListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectMessagePanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.ProjectMessageView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class ServerPushLoadingActivity extends AbstractActivity {
	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();
	private final Place destinationPlace;

	public ServerPushLoadingActivity(final Place destinationPlace) {
		this.destinationPlace = destinationPlace;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		validateConnection();

		final ProjectMessageView view = new ProjectMessagePanel();
		panel.setWidget(view);
		view.setMainMessage("Establishing connection to server...");

		SERVICE_PROVIDER.getServerPushClientService().onConnected(new ServerPushConnectionCallback() {
			@Override
			public void connected() {
				validateConnection();
			}

			@Override
			public void uncaughtExeption(final Throwable cause) {
				treatUnexpectedFailure(cause, "Could not connect to server: " + cause.getMessage());
			}
		});
		SERVICE_PROVIDER.getClientNotificationService().setNotificationParentWidget(view.asWidget());
	}

	@Override
	public void onStop() {
		SERVICE_PROVIDER.getClientNotificationService().clearNotificationParentWidget();
	}

	private void validateConnection() {
		if (!SERVICE_PROVIDER.getServerPushClientService().isConnected()) return;
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