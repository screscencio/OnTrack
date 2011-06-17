package br.com.oncast.ontrack.shared.services.communication;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;

public class ModelActionSyncRequest extends ProjectContextRequest {

	private final ModelAction action;
	private final boolean wasRollback;

	public ModelActionSyncRequest(final ModelAction action, final boolean wasRollback) {
		this.action = action;
		this.wasRollback = wasRollback;
	}

	public ModelAction getAction() {
		return action;
	}

	public boolean isRollback() {
		return wasRollback;
	}
}
