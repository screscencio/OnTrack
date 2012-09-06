package br.com.oncast.ontrack.server.services.serverPush.atmosphere;

import java.util.HashMap;
import java.util.Map;

import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.gwt.server.GwtAtmosphereResource;

import br.com.oncast.ontrack.server.services.serverPush.CometClientConnection;
import br.com.oncast.ontrack.server.services.serverPush.InternalConnectionListener;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushApi;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnection;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

public class OntrackAtmospherePushServer implements ServerPushApi {

	private final Map<ServerPushConnection, GwtAtmosphereResource> cometSessionMap = new HashMap<ServerPushConnection, GwtAtmosphereResource>();

	@Override
	public void pushEvent(final ServerPushEvent serverPushEvent, final ServerPushConnection client) {
		cometSessionMap.get(client.getClientId()).getBroadcaster().broadcast(serverPushEvent);
	}

	@Override
	public void setConnectionListener(final InternalConnectionListener connectionListener) {
		OntrackAtmosphereHandler.setConnectionListener(new AtmosphereConnectionListener() {

			@Override
			public void disconnected(final GwtAtmosphereResource resource) {
				final ServerPushConnection identifier = toResourceIdentifier(resource);
				cometSessionMap.remove(identifier);
				connectionListener.onClientDisconnected(identifier);
			}

			@Override
			public void connected(final GwtAtmosphereResource resource) {

				final ServerPushConnection resourceIdentifier = toResourceIdentifier(resource);
				setupBroadcaster(resource, resourceIdentifier);
				cometSessionMap.put(resourceIdentifier, resource);
				connectionListener.onClientConnected(resourceIdentifier);
			}

		});
	}

	private void setupBroadcaster(final GwtAtmosphereResource resource, final ServerPushConnection resourceIdentifier) {
		resource.getAtmosphereResource().setBroadcaster(BroadcasterFactory.getDefault().get(resourceIdentifier));
	}

	// TODO move to Value Obejct
	private ServerPushConnection toResourceIdentifier(final GwtAtmosphereResource resource) {
		return new CometClientConnection(String.valueOf(resource.getConnectionID()), resource.getSession().getId());
	}
}
