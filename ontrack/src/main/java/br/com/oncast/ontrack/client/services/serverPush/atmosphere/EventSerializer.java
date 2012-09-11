package br.com.oncast.ontrack.client.services.serverPush.atmosphere;

import org.atmosphere.gwt.client.AtmosphereGWTSerializer;
import org.atmosphere.gwt.client.SerialTypes;

import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

@SerialTypes(ServerPushEvent.class)
public abstract class EventSerializer extends AtmosphereGWTSerializer {}
