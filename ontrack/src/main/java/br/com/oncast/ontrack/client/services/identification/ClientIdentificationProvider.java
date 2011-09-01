package br.com.oncast.ontrack.client.services.identification;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ClientIdentificationProvider {

	private UUID clientId;

	public UUID getClientId() {
		if (clientId != null) return clientId;
		return clientId = new UUID();
	}
}
