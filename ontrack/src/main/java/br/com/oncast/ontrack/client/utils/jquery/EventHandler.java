package br.com.oncast.ontrack.client.utils.jquery;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public abstract class EventHandler {
	@SuppressWarnings("unused")
	// IMPORTANT This field is used natively through JavaScript so that handlers can be unbounded individually.
	private final String registrationId = new UUID().toStringRepresentation();

	public abstract void handle(Event e);
}
