package br.com.oncast.ontrack.client.services.serverPush;

import java.io.Serializable;
import java.util.List;

import net.zschech.gwt.comet.client.CometClient;
import net.zschech.gwt.comet.client.CometListener;
import net.zschech.gwt.comet.client.CometSerializer;
import br.com.oncast.ontrack.client.services.requestDispatch.DispatchCallback;
import br.com.oncast.ontrack.client.services.requestDispatch.RequestDispatchService;

import com.google.gwt.core.client.GWT;

public class GwtCometClient {

	private final RequestDispatchService requestDispatchService;
	private final ServerPushClientEventListener serverPushClientEventListener;

	public GwtCometClient(final RequestDispatchService requestDispatchService, final ServerPushClientEventListener serverPushClientEventListener) {
		this.requestDispatchService = requestDispatchService;
		this.serverPushClientEventListener = serverPushClientEventListener;
	}

	public void connectToServer() {
		requestDispatchService.startListeningServerPushes(new DispatchCallback<Void>() {
			@Override
			public void onRequestCompletition(final Void response) {
				startListening();
			}

			@Override
			public void onFailure(final Throwable caught) throws ServerPushException {
				// FIXME Should this exception be checked?
				throw new ServerPushException("Problems while trying to listen to server pushes.", caught);
			}
		});
	}

	protected void startListening() {
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
		final CometClient client = new CometClient(GWT.getModuleBaseURL() + "comet", serializer, listener);
		client.start();
	}
}
