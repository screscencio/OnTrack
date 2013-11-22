package br.com.oncast.ontrack.client.services.serverPush.atmosphere;

import br.com.oncast.ontrack.client.services.serverPush.ServerPushClient;
import br.com.oncast.ontrack.shared.services.url.URLBuilder;

import org.atmosphere.gwt.client.AtmosphereClient;
import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.AtmosphereListener;

import com.google.gwt.core.client.GWT;

public class OntrackAtmosphereClient implements ServerPushClient {

	private final AtmosphereClient client;

	public OntrackAtmosphereClient(final AtmosphereListener listener) {
		client = new AtmosphereClient(URLBuilder.ATMOSPHERE_URL, (AtmosphereGWTSerializer) GWT.create(EventSerializer.class), listener, false);
		client.setConnectionTimeout(9000);
		client.setReconnectionTimeout(50);
		client.setReconnectionCount(0);
	}

	@Override
	public void start() {
		client.start();
	}

	@Override
	public boolean isRunning() {
		return client.isRunning();
	}

	@Override
	public void stop() {
		client.stop();
	}

	@Override
	public String getConnectionId() {
		return client.getConnectionUUID();
	}
}
