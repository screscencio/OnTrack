package br.com.oncast.ontrack.client.services.communication.requestDispatch;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommunicationRpcServiceAsync {

	void transmitAction(ModelAction action, AsyncCallback<Void> callback);

	void loadProject(AsyncCallback<Project> callback);
}
