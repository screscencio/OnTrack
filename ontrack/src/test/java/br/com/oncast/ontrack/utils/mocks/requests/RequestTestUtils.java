package br.com.oncast.ontrack.utils.mocks.requests;

import static br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils.createProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils;

public class RequestTestUtils {

	public static ModelActionSyncRequest createModelActionSyncRequest(final UUID clientId) {
		final ModelActionSyncRequest modelActionSyncRequest = new ModelActionSyncRequest(clientId, createProjectRepresentation(),
				ActionTestUtils.getSomeActions());
		return modelActionSyncRequest;
	}
}
