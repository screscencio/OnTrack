package br.com.oncast.ontrack.client.services.requestDispatch;

import java.util.List;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListRequest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

// TODO +++Implement/Refactor dispatch method to receive generic "Requests" and asynchronously return "Responses".
// TODO Provide a centralized exception handling mechanism in which you can register exception handlers.
public class RequestDispatchServiceImpl implements RequestDispatchService {

	private final CommunicationRpcServiceAsync rpcServiceAsync = GWT.create(CommunicationRpcService.class);

	@Override
	public void dispatch(final ProjectContextRequest projectContextRequest, final DispatchCallback<ProjectContext> dispatchCallback) {
		rpcServiceAsync.loadProjectForClient(projectContextRequest, new AsyncCallback<Project>() {

			@Override
			public void onSuccess(final Project project) {
				dispatchCallback.onRequestCompletition(new ProjectContext(project));
			}

			@Override
			public void onFailure(final Throwable caught) {
				dispatchCallback.onFailure(caught);
				caught.printStackTrace();
			}
		});
	}

	@Override
	public void dispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<Void> dispatchCallback) {
		rpcServiceAsync.transmitAction(modelActionSyncRequest, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(final Void result) {
				dispatchCallback.onRequestCompletition(result);
			}

			@Override
			public void onFailure(final Throwable caught) {
				dispatchCallback.onFailure(caught);
				caught.printStackTrace();
			}
		});
	}

	@Override
	public void dispatch(final ProjectCreationRequest projectCreationRequest, final DispatchCallback<ProjectRepresentation> dispatchCallback) {
		rpcServiceAsync.createProject(projectCreationRequest, new AsyncCallback<ProjectRepresentation>() {

			@Override
			public void onSuccess(final ProjectRepresentation result) {
				dispatchCallback.onRequestCompletition(result);
			}

			@Override
			public void onFailure(final Throwable caught) {
				dispatchCallback.onFailure(caught);
				caught.printStackTrace();
			}
		});
	}

	@Override
	public void dispatch(final ProjectListRequest projectListRequest, final DispatchCallback<List<ProjectRepresentation>> dispatchCallback) {
		rpcServiceAsync.retrieveProjectList(new AsyncCallback<List<ProjectRepresentation>>() {

			@Override
			public void onSuccess(final List<ProjectRepresentation> result) {
				dispatchCallback.onRequestCompletition(result);
			}

			@Override
			public void onFailure(final Throwable caught) {
				dispatchCallback.onFailure(caught);
				caught.printStackTrace();
			}
		});
	}
}
