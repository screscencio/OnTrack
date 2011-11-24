package br.com.oncast.ontrack.client.services.requestDispatch;

import java.util.List;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationRequest;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommunicationRpcServiceAsync {

	void transmitAction(ModelActionSyncRequest modelActionSyncRequest, AsyncCallback<Void> callback);

	void loadProjectForClient(final ProjectContextRequest projectContextRequest, AsyncCallback<Project> callback);

	void createProject(ProjectCreationRequest projectCreationRequest, AsyncCallback<ProjectRepresentation> asyncCallback);

	void retrieveProjectList(AsyncCallback<List<ProjectRepresentation>> asyncCallback);
}
