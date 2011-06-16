package br.com.oncast.ontrack.client.services.communication.rpc;

import br.com.oncast.ontrack.shared.project.Project;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommunicationRpcServiceAsync {

	void transmitAction(ScopeAction action, AsyncCallback<Void> callback);

	void loadProject(AsyncCallback<Project> callback);
}
