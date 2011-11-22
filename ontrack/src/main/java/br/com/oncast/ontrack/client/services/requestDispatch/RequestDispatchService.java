package br.com.oncast.ontrack.client.services.requestDispatch;

import java.util.List;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListRequest;

public interface RequestDispatchService {

	public void dispatch(final ProjectContextRequest projectContextRequest, final DispatchCallback<ProjectContext> dispatchCallback);

	public void dispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<Void> dispatchCallback);

	public void dispatch(final ProjectCreationRequest projectCreationRequest, final DispatchCallback<ProjectRepresentation> dispatchCallback);

	public void dispatch(ProjectListRequest projectListRequest, DispatchCallback<List<ProjectRepresentation>> dispatchCallback);

}