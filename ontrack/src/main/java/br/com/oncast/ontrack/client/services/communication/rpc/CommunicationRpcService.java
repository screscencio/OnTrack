package br.com.oncast.ontrack.client.services.communication.rpc;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("communicationService")
public interface CommunicationRpcService extends RemoteService {

	void transmitAction(ModelAction action, boolean isRollback);

	public Project loadProject();
}
