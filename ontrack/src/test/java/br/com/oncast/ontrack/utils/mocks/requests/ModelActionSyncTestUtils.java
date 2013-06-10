package br.com.oncast.ontrack.utils.mocks.requests;

import static br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils.createOneValidAction;
import static br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils.createSomeActions;
import static br.com.oncast.ontrack.utils.model.ProjectTestUtils.createRepresentation;

import org.mockito.Mockito;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionSync.ModelActionSyncEvent;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;

public class ModelActionSyncTestUtils {

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

	public static ModelActionSyncEvent createModelActionSyncEvent() {
		return new ModelActionSyncEvent(createRepresentation().getId(), createSomeActions(), Mockito.mock(ActionContext.class), -1);
	}
}
