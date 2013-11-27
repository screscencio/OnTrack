package br.com.oncast.ontrack.client.services.serverPush.atmosphere;

import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.SerialTypes;

@SerialTypes(ServerPushEvent.class)
public abstract class EventSerializer extends AtmosphereGWTSerializer {}
