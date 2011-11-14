package br.com.oncast.ontrack.client.services.requestDispatch;

import br.com.oncast.ontrack.shared.exceptions.business.BusinessException;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("requestDispatchService")
public interface CommunicationRpcService extends RemoteService {

	void transmitAction(ModelActionSyncRequest modelActionSyncRequest) throws BusinessException;

	Project loadProject(ProjectContextRequest projectContextRequest) throws BusinessException;
}
