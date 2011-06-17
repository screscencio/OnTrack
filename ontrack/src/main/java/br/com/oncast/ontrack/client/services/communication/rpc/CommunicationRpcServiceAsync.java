package br.com.oncast.ontrack.client.services.communication.rpc;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.project.Project;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommunicationRpcServiceAsync {

	void transmitAction(ModelAction<?> action, boolean isRollback, AsyncCallback<Void> callback);

	void loadProject(AsyncCallback<Project> callback);
}
