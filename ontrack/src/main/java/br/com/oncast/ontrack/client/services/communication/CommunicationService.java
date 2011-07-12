package br.com.oncast.ontrack.client.services.communication;

import br.com.oncast.ontrack.client.services.communication.rpc.CommunicationRpcService;
import br.com.oncast.ontrack.client.services.communication.rpc.CommunicationRpcServiceAsync;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.services.communication.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.communication.ProjectContextRequest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

// TODO +++Implement/Refactor dispatch method to receive generic "Requests" and asynchronously return "Responses".
public class CommunicationService {

	final CommunicationRpcServiceAsync rpcServiceAsync = GWT.create(CommunicationRpcService.class);

	public void dispatch(final ProjectContextRequest projectContextRequest, final DispatchCallback<ProjectContext> dispatchCallback) {
		rpcServiceAsync.loadProject(new AsyncCallback<Project>() {

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

	public void dispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<Void> dispatchCallback) {
		rpcServiceAsync.transmitAction(modelActionSyncRequest.getAction(), new AsyncCallback<Void>() {

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
}
