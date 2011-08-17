package br.com.oncast.ontrack.server.services.actionSync;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;

public interface ActionBroadcastService {

	void broadcast(ModelAction action);
}
