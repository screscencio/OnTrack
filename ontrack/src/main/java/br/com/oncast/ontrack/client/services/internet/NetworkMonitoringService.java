package br.com.oncast.ontrack.client.services.internet;

import java.util.ArrayList;
import java.util.List;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.client.FailureHandler;
import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.alerting.AlertConfirmationListener;
import br.com.oncast.ontrack.client.services.alerting.AlertRegistration;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.alerting.ConfirmationAlertRegister;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.ui.places.loading.ServerPushConnectionCallback;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.StatusCodeException;

public class NetworkMonitoringService {

	private boolean connected = true;
	private final ClientErrorMessages messages;
	private final ClientAlertingService alertingService;
	private ConfirmationAlertRegister errorAlertConfirmation;
	private final ServerPushClientService serverPushClientService;
	private final List<ConnectionListener> connectionListeners = new ArrayList<ConnectionListener>();
	private AlertRegistration infoAlertRegistration = null;
	private final Timer connectionVerificationTimer = new Timer() {

		@Override
		public void run() {
			if (connected) return;

			// FIXME LOBO i18n
			infoAlertRegistration = alertingService.showInfo("Trying to reconnect...");

			if (isInternetAvailable()) {
				if (serverPushClientService.isConnected()) onConnectionRecovered();
				else serverPushClientService.connect();
			}
			this.schedule(3200);
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

		if (errorAlertConfirmation != null) {
			errorAlertConfirmation.hide(false);
			errorAlertConfirmation = null;
		}
		if (infoAlertRegistration != null) {
			infoAlertRegistration.hide();
			infoAlertRegistration = null;
		}

		// FIXME LOBO i18n
		alertingService.showInfo("Engaging server!");

		notifyConnectionRecovered();
	}

	protected native boolean isInternetAvailable() /*-{
		return navigator.onLine;
	}-*/;

	private void alertMissingConnection() {
		errorAlertConfirmation = alertingService.showErrorWithConfirmation(messages.connectionLost(), new AlertConfirmationListener() {
			@Override
			public void onConfirmation() {
				connectionVerificationTimer.cancel();
				connectionVerificationTimer.run();
			}
		});
	}

	public void addConnectionListener(final ConnectionListener connectionListener) {
		connectionListeners.add(connectionListener);
	}

	private void notifyConnectionRecovered() {
		for (final ConnectionListener listener : connectionListeners)
			listener.onConnectionRecovered();
	}
}
