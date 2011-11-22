package br.com.oncast.ontrack.client.services.requestDispatch;

import java.util.List;

import br.com.oncast.ontrack.shared.exceptions.business.BusinessException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveProjectListException;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationRequest;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("requestDispatchService")
public interface CommunicationRpcService extends RemoteService {

	void transmitAction(ModelActionSyncRequest modelActionSyncRequest) throws BusinessException;

	Project loadProject(ProjectContextRequest projectContextRequest) throws BusinessException;

	ProjectRepresentation createProject(ProjectCreationRequest projectCreationRequest) throws BusinessException;

	List<ProjectRepresentation> retrieveProjectList() throws BusinessException, UnableToRetrieveProjectListException;
}
