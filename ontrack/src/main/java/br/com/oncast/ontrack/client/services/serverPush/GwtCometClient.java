/*
 * DECISION (14/09/2011) Rodrigo decided to keep this implementation, for now fixing only excessive Alerts (mainly in browser crashes apparently caused by
 * session timeout), even though some workarounds are going to be needed. A ping will be implemented to keep the server session alive and all other errors will
 * still show an alert.
 */

package br.com.oncast.ontrack.client.services.serverPush;

import java.io.Serializable;
import java.util.List;

import net.zschech.gwt.comet.client.CometClient;
import net.zschech.gwt.comet.client.CometListener;
import net.zschech.gwt.comet.client.CometSerializer;
import br.com.oncast.ontrack.client.services.identification.ClientIdentificationProvider;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;
import br.com.oncast.ontrack.shared.services.url.URLBuilder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

class GwtCometClient implements ServerPushClient {

	private static final int PING_TIMER_DELAY = 1000 * 60 * 10;

	private final CometClient client;

	private final GwtCometPingServiceAsync pingService = GWT.create(GwtCometPingService.class);

	public GwtCometClient(final ClientIdentificationProvider clientIdentificationProvider, final ServerPushClientEventListener serverPushClientEventListener) {
		client = new CometClient(URLBuilder.SERVER_PUSH_COMET_URL,
				clientIdentificationProvider.getClientId().toStringRepresentation(),
				GWT.<CometSerializer> create(ServerPushSerializer.class),
				new CometListener() {

					@Override
					public void onConnected(final int heartbeat) {
						serverPushClientEventListener.onConnected();
					}

					@Override
					public void onDisconnected() {
						serverPushClientEventListener.onDisconnected();
					}

					@Override
					public void onHeartbeat() {}

					@Override
					public void onRefresh() {}

					@Override
					public void onError(final Throwable exception, final boolean connected) {
						// IMPORTANT This is a workaround so that Alerts are not shown when manually reloading the application. This is done because
						// there is no
						// safe
						// way to determine if a error was caused by the reload itself and the library errors throw only one type of exception.
						new Timer() {

							@Override
							public void run() {
								serverPushClientEventListener.onError(exception);
							}
						}.schedule(500);
					}

					@Override
					public void onMessage(final List<? extends Serializable> messages) {
						processIncomingMessages(serverPushClientEventListener, messages);
					}
				});

		// IMPORTANT This timer is only used to keep the session alive. Should be removed when changing ServerPush implementation.
		new Timer() {

			@Override
			public void run() {
				pingService.ping(new AsyncCallback<Void>() {

					@Override
					public void onSuccess(final Void result) {}

					@Override
					public void onFailure(final Throwable caught) {}
				});
			}
		}.scheduleRepeating(PING_TIMER_DELAY);
	}

	@Override
	public void start() {
		client.start();
	}

	@Override
	public boolean isRunning() {
		return client.isRunning();
	}

	private void processIncomingMessages(final ServerPushClientEventListener serverPushClientEventListener, final List<? extends Serializable> messages) {
		for (final Serializable message : messages) {
			if (message instanceof ServerPushEvent) serverPushClientEventListener.onEvent((ServerPushEvent) message);
			else {
				// TODO Threat unknown message received by the comet server push library.
			}
		}
	}

	@Override
	public void stop() {
		client.stop();
	}
}
