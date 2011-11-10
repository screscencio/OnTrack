package br.com.oncast.ontrack.client.services.requestDispatch;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommunicationRpcServiceAsync {

	void transmitAction(ModelActionSyncRequest modelActionSyncRequest, AsyncCallback<Void> callback);

	void loadProject(AsyncCallback<Project> callback);
}
