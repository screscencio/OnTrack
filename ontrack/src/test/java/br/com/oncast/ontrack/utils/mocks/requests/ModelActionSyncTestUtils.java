package br.com.oncast.ontrack.utils.mocks.requests;

import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.server.services.exportImport.xml.UserActionTestUtils;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils;

import static br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils.createOneValidAction;
import static br.com.oncast.ontrack.utils.model.ProjectTestUtils.createRepresentation;

public class ModelActionSyncTestUtils {

	public static ModelActionSyncRequest createModelActionSyncRequest() {
		return new ModelActionSyncRequest(createRepresentation().getId(), UserActionTestUtils.create(ActionTestUtils.createSomeActions()));
	}

	public static ModelActionSyncRequest createModelActionSyncRequestWithOneAction(final UUID projectId) {
		return new ModelActionSyncRequest(projectId, UserActionTestUtils.create(createOneValidAction(), DefaultAuthenticationCredentials.USER_ID, projectId));
	}

	public static ProjectContextRequest createProjectContextRequest() {
		return new ProjectContextRequest(createRepresentation().getId());
	}

	public static ProjectContextRequest createProjectContextRequest(final UUID projectId) {
		return new ProjectContextRequest(projectId);
	}

}
