package br.com.oncast.ontrack.server.services.actionBroadcast;

import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

public interface ActionBroadcastService {

	void broadcast(ModelActionSyncRequest modelActionSyncRequest);
}
