package br.com.oncast.ontrack.client.services.requestDispatch;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;

public interface RequestDispatchService {

	public void dispatch(final ProjectContextRequest projectContextRequest, final DispatchCallback<ProjectContext> dispatchCallback);

	public void dispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<Void> dispatchCallback);
}