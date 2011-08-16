package br.com.oncast.ontrack.client.services.serverPush;

import java.io.Serializable;
import java.util.List;

import net.zschech.gwt.comet.client.CometClient;
import net.zschech.gwt.comet.client.CometListener;
import net.zschech.gwt.comet.client.CometSerializer;
import br.com.oncast.ontrack.shared.config.UriConfigurations;

import com.google.gwt.core.client.GWT;

public class GwtCometClient {

	private final ServerPushClientEventListener serverPushClientEventListener;

	public GwtCometClient(final ServerPushClientEventListener serverPushClientEventListener) {
		this.serverPushClientEventListener = serverPushClientEventListener;
	}

	public void connectToServer() {
		final CometListener listener = new CometListener() {
			@Override
			public void onConnected(final int heartbeat) {
				System.out.println("Server push connected.");
			}

			@Override
			public void onDisconnected() {
				System.out.println("Server push disconnected.");
			}

			@Override
			public void onHeartbeat() {
				System.out.println("[" + System.currentTimeMillis() + "] A heartbeat arrived from server: the connection is still alive.");
			}

			@Override
			public void onRefresh() {
				System.out.println("Refreshing server push connection...");
			}

			@Override
			public void onError(final Throwable exception, final boolean connected) {
				throw new RuntimeException(exception);
			}

			@Override
			public void onMessage(final List<? extends Serializable> messages) {
				for (final Serializable message : messages) {
					serverPushClientEventListener.onEvent(message);
				}
			}
		};

		System.out.println("Starting server push listener...");
		final CometSerializer serializer = GWT.create(ServerPushSerializer.class);
		final CometClient client = new CometClient(UriConfigurations.SERVER_PUSH_COMET_URL, serializer, listener);
		client.start();
	}
}
