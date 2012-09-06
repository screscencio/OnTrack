package br.com.oncast.ontrack.server.services.serverPush.atmosphere;

import org.atmosphere.gwt.server.GwtAtmosphereResource;

public interface AtmosphereConnectionListener {

	void connected(GwtAtmosphereResource resource);

	void disconnected(GwtAtmosphereResource cometResponse);

}
