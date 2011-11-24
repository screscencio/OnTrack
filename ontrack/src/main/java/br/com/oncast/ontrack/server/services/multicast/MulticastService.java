package br.com.oncast.ontrack.server.services.multicast;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

public interface MulticastService {

	void multicastActionSyncRequest(ModelActionSyncRequest modelActionSyncRequest);

	void broadcastProjectCreation(ProjectRepresentation projectRepresentation);

}
