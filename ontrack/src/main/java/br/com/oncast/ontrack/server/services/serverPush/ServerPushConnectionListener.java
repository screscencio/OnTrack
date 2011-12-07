package br.com.oncast.ontrack.server.services.serverPush;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ServerPushConnectionListener {

	void onClientConnected(UUID clientId, String sessionId);

	void onClientDisconnected(UUID clientId, String sessionId);
}
