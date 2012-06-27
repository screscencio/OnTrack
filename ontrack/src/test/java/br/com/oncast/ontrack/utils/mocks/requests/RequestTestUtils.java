package br.com.oncast.ontrack.utils.mocks.requests;

import static br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils.createOneValidAction;
import static br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils.createSomeActions;
import static br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils.createRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;

public class RequestTestUtils {

	public static ModelActionSyncRequest createModelActionSyncRequest() {
		return new ModelActionSyncRequest(createRepresentation(), createSomeActions());
	}

	public static ModelActionSyncRequest createModelActionSyncRequestWithOneAction() {
		return new ModelActionSyncRequest(createRepresentation(), createOneValidAction());
	}

	public static ModelActionSyncRequest createModelActionSyncRequestWithOneAction(final UUID projectId) {
		return new ModelActionSyncRequest(createRepresentation(projectId), createOneValidAction());
	}

	public static ModelActionSyncRequest createModelActionSyncRequest(final UUID projectId) {
		return new ModelActionSyncRequest(createRepresentation(projectId), createSomeActions());
	}

	public static ProjectContextRequest createProjectContextRequest() {
		return new ProjectContextRequest(createRepresentation().getId());
	}

	public static ProjectContextRequest createProjectContextRequest(final UUID projectId) {
		return new ProjectContextRequest(projectId);
	}
}
