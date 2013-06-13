package br.com.oncast.ontrack.client.services.internet;

import java.util.ArrayList;
import java.util.List;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.client.FailureHandler;
import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.alerting.AlertRegistration;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.ui.places.loading.ServerPushConnectionCallback;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.StatusCodeException;

public class NetworkMonitoringService {

	private final ClientErrorMessages messages;
	private final ClientAlertingService alertingService;
	private final ServerPushClientService serverPushClientService;

	private AlertRegistration tryingToReconnectAlertRegistration;
	private AlertRegistration establishingConnectionAlertRegistration;
	private AlertRegistration errorAlertConfirmation;

	private final List<ConnectionListener> connectionListeners = new ArrayList<ConnectionListener>();

	private boolean connected = true;

	private final Timer connectionVerificationTimer = new Timer() {
		@Override
		public void run() {
			if (connected) return;

			tryingToReconnectAlertRegistration = alertingService.showInfo(messages.tryingToReconnect());
			serverPushClientService.reconnect();
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
				onConnectionRecovered();
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

		notifyConnectionLost();
		alertMissingConnection();
		connectionVerificationTimer.cancel();
		connectionVerificationTimer.schedule(5000);
	}

	private void onConnectionRecovered() {
		if (connected) return;
		connected = true;

		connectionVerificationTimer.cancel();

		if (errorAlertConfirmation != null) {
			errorAlertConfirmation.hide();
			errorAlertConfirmation = null;
		}
		if (tryingToReconnectAlertRegistration != null) {
			tryingToReconnectAlertRegistration.hide();
			tryingToReconnectAlertRegistration = null;
		}

		if (establishingConnectionAlertRegistration != null) establishingConnectionAlertRegistration.hide();
		establishingConnectionAlertRegistration = alertingService.showInfo(messages.establishingConnection());

		notifyConnectionRecovered();
	}

	protected native boolean isInternetAvailable() /*-{
		return navigator.onLine;
	}-*/;

	private void alertMissingConnection() {
		if (errorAlertConfirmation != null) return;
		errorAlertConfirmation = alertingService.showBlockingError(messages.offilineMode());
	}

	public void addConnectionListener(final ConnectionListener connectionListener) {
		connectionListeners.add(connectionListener);
	}

	private void notifyConnectionRecovered() {
		for (final ConnectionListener listener : connectionListeners)
			listener.onConnectionRecovered();
	}

	private void notifyConnectionLost() {
		for (final ConnectionListener listener : connectionListeners)
			listener.onConnectionLost();
	}
}
