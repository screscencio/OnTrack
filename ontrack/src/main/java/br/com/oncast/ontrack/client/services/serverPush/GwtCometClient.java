package br.com.oncast.ontrack.client.services.serverPush;

import java.io.Serializable;
import java.util.List;

import net.zschech.gwt.comet.client.CometClient;
import net.zschech.gwt.comet.client.CometListener;
import net.zschech.gwt.comet.client.CometSerializer;
import br.com.oncast.ontrack.shared.config.UriConfigurations;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

import com.google.gwt.core.client.GWT;

class GwtCometClient implements ServerPushClient {

	private final CometClient client;

	public GwtCometClient(final ServerPushClientEventListener serverPushClientEventListener) {
		client = new CometClient(UriConfigurations.SERVER_PUSH_COMET_URL, GWT.<CometSerializer> create(ServerPushSerializer.class), new CometListener() {

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
				serverPushClientEventListener.onError(exception);
			}

			@Override
			public void onMessage(final List<? extends Serializable> messages) {
				processIncomingMessages(serverPushClientEventListener, messages);
			}
		});
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
				// TODO Threat unknown message received by the comet server push lib.
			}
		}
	}

	@Override
	public void stop() {
		client.stop();
	}
}
