package br.com.oncast.ontrack.client.services.internet;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.client.FailureHandler;
import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.alerting.AlertConfirmationListener;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.alerting.ConfirmationAlertRegister;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.ui.places.loading.ServerPushConnectionCallback;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.StatusCodeException;

public class NetworkMonitoringService {

	private final ClientErrorMessages messages;
	private final ClientAlertingService alertingService;
	private boolean connected = true;
	private ConfirmationAlertRegister alertConfirmation;
	private final ServerPushClientService serverPushClientService;
	private final Timer connectionVerificationTimer = new Timer() {

		@Override
		public void run() {
			// FIXME LOBO i18n
			alertingService.showInfo("Trying to reconnect...");

			if (isInternetAvailable()) serverPushClientService.connect();
			this.schedule(3000);
		}
	};

	public NetworkMonitoringService(final DispatchService requestDispatchService, final ServerPushClientService serverPushClientService,
			final ClientAlertingService clientAlertingService, final ClientErrorMessages clientErrorMessages) {
		this.serverPushClientService = serverPushClientService;
		alertingService = clientAlertingService;
		messages = clientErrorMessages;

		requestDispatchService.addFailureHandler(StatusCodeException.class, new FailureHandler<StatusCodeException>() {

			@Override
			public void handle(final StatusCodeException caught) {
				if (caught.getStatusCode() != 0) return;
				onConnectionLost();
			}
		});

		requestDispatchService.addFailureHandler(InvocationException.class, new FailureHandler<InvocationException>() {

			@Override
			public void handle(final InvocationException caught) {
				onConnectionLost();
			}
		});

		serverPushClientService.addConnectionListener(new ServerPushConnectionCallback() {

			@Override
			public void uncaughtExeption(final Throwable cause) {
				if (!serverPushClientService.isConnected()) disconnected();
			}

			@Override
			public void connected() {
				if (!connected) onConnectionRecovered();
			}

			@Override
			public void disconnected() {
				onConnectionLost();
			}
		});
	}

	private void onConnectionLost() {
		if (!connected) return;
		connected = false;

		alertMissingConnection();
		connectionVerificationTimer.run();
	}

	private void onConnectionRecovered() {
		if (connected) return;
		connected = true;

		connectionVerificationTimer.cancel();

		if (alertConfirmation != null) {
			alertConfirmation.hide(false);
			alertConfirmation = null;
		}

		// FIXME LOBO i18n
		alertingService.showSuccess("Connection recovered!");
	}

	protected native boolean isInternetAvailable() /*-{
		return navigator.onLine;
	}-*/;

	private void alertMissingConnection() {
		alertConfirmation = alertingService.showErrorWithConfirmation(messages.connectionLost(), new AlertConfirmationListener() {
			@Override
			public void onConfirmation() {
				connectionVerificationTimer.cancel();
				connectionVerificationTimer.run();
			}
		});
	}

}
