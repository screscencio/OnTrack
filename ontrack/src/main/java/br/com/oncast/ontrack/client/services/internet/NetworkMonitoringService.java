package br.com.oncast.ontrack.client.services.internet;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.client.FailureHandler;

import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.alerting.AlertRegistration;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.metrics.ClientMetricsService;
import br.com.oncast.ontrack.client.services.metrics.TimeTrackingEvent;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.ui.places.loading.ServerPushConnectionCallback;
import br.com.oncast.ontrack.shared.metrics.MetricsCategories;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class NetworkMonitoringService {

	private final ClientErrorMessages messages;
	private final ClientAlertingService alertingService;
	private final ServerPushClientService serverPushClientService;
	private final ClientMetricsService metrics;

	private AlertRegistration tryingToReconnectAlertRegistration;
	private AlertRegistration establishingConnectionAlertRegistration;
	private AlertRegistration errorAlertConfirmation;

	private final List<ConnectionListener> connectionListeners = new ArrayList<ConnectionListener>();

	private boolean connected = true;

	private TimeTrackingEvent timeTracking;

	private final Timer connectionVerificationTimer = new Timer() {
		@Override
		public void run() {
			if (connected) return;

			tryingToReconnectAlertRegistration = alertingService.showInfo(messages.tryingToReconnect());
			serverPushClientService.reconnect();
			this.schedule(3200);
		}
	};

	public NetworkMonitoringService(final DispatchService requestDispatchService, final ServerPushClientService serverPushClientService, final ClientAlertingService clientAlertingService,
			final ClientErrorMessages clientErrorMessages, final ClientMetricsService metrics) {
		this.serverPushClientService = serverPushClientService;
		alertingService = clientAlertingService;
		messages = clientErrorMessages;
		this.metrics = metrics;

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

		Window.addCloseHandler(new CloseHandler<Window>() {
			@Override
			public void onClose(final CloseEvent<Window> event) {
				if (timeTracking != null) timeTracking.end();
			}
		});
	}

	private void onConnectionLost() {
		if (!connected) return;
		connected = false;
		if (timeTracking != null) timeTracking.end();
		timeTracking = metrics.startTimeTracking(MetricsCategories.CLIENT_CONNECTION_STATUS, "offline");
		metrics.onConnectionLost();

		notifyConnectionLost();
		alertMissingConnection();
		connectionVerificationTimer.cancel();
		connectionVerificationTimer.schedule(5000);
	}

	private void onConnectionRecovered() {
		if (connected) return;
		connected = true;
		if (timeTracking != null) timeTracking.end();
		timeTracking = metrics.startTimeTracking(MetricsCategories.CLIENT_CONNECTION_STATUS, "online");

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

	public HandlerRegistration addConnectionListener(final ConnectionListener connectionListener) {
		connectionListeners.add(connectionListener);
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				connectionListeners.remove(connectionListener);
			}
		};
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
