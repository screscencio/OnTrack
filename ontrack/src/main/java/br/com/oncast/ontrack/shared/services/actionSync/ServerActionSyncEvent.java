package br.com.oncast.ontrack.shared.services.actionSync;

import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

public class ServerActionSyncEvent implements ServerPushEvent {

	private static final long serialVersionUID = 1L;

	private ModelActionSyncRequest modelActionSyncRequest;

	protected ServerActionSyncEvent() {}

	public ServerActionSyncEvent(final ModelActionSyncRequest modelActionSyncRequest) {
		this.modelActionSyncRequest = modelActionSyncRequest;
	}

	public ModelActionSyncRequest getModelActionSyncRequest() {
		return modelActionSyncRequest;
	}
}
