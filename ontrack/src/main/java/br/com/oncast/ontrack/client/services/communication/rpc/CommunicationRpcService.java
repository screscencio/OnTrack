package br.com.oncast.ontrack.client.services.communication.rpc;

import br.com.oncast.ontrack.shared.project.Project;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("communicationService")
public interface CommunicationRpcService extends RemoteService {

	public void transmitAction(ScopeAction action);

	public Project loadProject();
}
