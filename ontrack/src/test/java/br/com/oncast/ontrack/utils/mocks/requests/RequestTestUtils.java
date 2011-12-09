package br.com.oncast.ontrack.utils.mocks.requests;

import static br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils.createOneValidAction;
import static br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils.createSomeActions;
import static br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils.createRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;

public class RequestTestUtils {

	public static ModelActionSyncRequest createModelActionSyncRequest() {
		return new ModelActionSyncRequest(new UUID(), createRepresentation(), createSomeActions());
	}

	public static ModelActionSyncRequest createModelActionSyncRequestWithOneAction() {
		return new ModelActionSyncRequest(new UUID(), createRepresentation(), createOneValidAction());
	}

	public static ModelActionSyncRequest createModelActionSyncRequest(final UUID clientId) {
		return new ModelActionSyncRequest(clientId, createRepresentation(), createSomeActions());
	}

	public static ModelActionSyncRequest createModelActionSyncRequest(final UUID clientId, final long projectId) {
		return new ModelActionSyncRequest(clientId, createRepresentation(projectId), createSomeActions());
	}

	public static ProjectContextRequest createProjectContextRequest() {
		return new ProjectContextRequest(new UUID(), createRepresentation().getId());
	}
}
