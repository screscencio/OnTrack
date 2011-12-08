package br.com.oncast.ontrack.utils.mocks.requests;

import static br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils.createProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils;

public class RequestTestUtils {

	public static ModelActionSyncRequest createModelActionSyncRequest(final UUID clientId) {
		return new ModelActionSyncRequest(clientId, createProjectRepresentation(), ActionTestUtils.createSomeActions());
	}

	public static ModelActionSyncRequest createModelActionSyncRequest() {
		return new ModelActionSyncRequest(new UUID(), createProjectRepresentation(), ActionTestUtils.createSomeActions());
	}

	public static ModelActionSyncRequest createModelActionSyncRequestWithOneAction() {
		return new ModelActionSyncRequest(new UUID(), createProjectRepresentation(), ActionTestUtils.createOneValidAction());
	}

	public static ProjectContextRequest createProjectContextRequest() {
		return new ProjectContextRequest(new UUID(), createProjectRepresentation().getId());
	}
}
