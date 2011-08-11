package br.com.oncast.ontrack.shared.services.requestDispatch;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;

public class ModelActionSyncRequest extends ProjectContextRequest {

	private final ModelAction action;

	public ModelActionSyncRequest(final ModelAction action) {
		this.action = action;
	}

	public ModelAction getAction() {
		return action;
	}
}
