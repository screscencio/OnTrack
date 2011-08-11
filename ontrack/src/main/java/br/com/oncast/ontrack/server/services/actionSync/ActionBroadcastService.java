package br.com.oncast.ontrack.server.services.actionSync;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;

// FIXME Review the name and responsibility of this interface. Should it control sync or broadcast of actions?
public interface ActionBroadcastService {

	void broadcast(ModelAction action);

}
