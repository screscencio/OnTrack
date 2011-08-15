package br.com.oncast.ontrack.shared.services.actionSync;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

public class ServerActionSyncEvent implements ServerPushEvent {

	private static final long serialVersionUID = 1L;

	private ModelAction action;

	protected ServerActionSyncEvent() {}

	public ServerActionSyncEvent(final ModelAction action) {
		this.action = action;
	}

	public ModelAction getAction() {
		return action;
	}
}
