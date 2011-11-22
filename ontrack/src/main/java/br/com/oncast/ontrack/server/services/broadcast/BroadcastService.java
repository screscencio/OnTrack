package br.com.oncast.ontrack.server.services.broadcast;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

public interface BroadcastService {

	void broadcastActionSyncRequest(ModelActionSyncRequest modelActionSyncRequest);

	void broadcastProjectCreation(ProjectRepresentation projectRepresentation);
}
