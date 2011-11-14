package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.oncast.ontrack.client.services.requestDispatch.CommunicationRpcService;
import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.ServerBusinessLogicLocator;
import br.com.oncast.ontrack.shared.exceptions.business.BusinessException;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CommunicationRpcServiceImpl extends RemoteServiceServlet implements CommunicationRpcService {

	private static final long serialVersionUID = 1L;

	private static final BusinessLogic BUSINESS = ServerBusinessLogicLocator.getInstance().getBusinessLogic();

	@Override
	public void transmitAction(final ModelActionSyncRequest modelActionSyncRequest) throws BusinessException {
		BUSINESS.handleIncomingActionSyncRequest(modelActionSyncRequest);
	}

	@Override
	public Project loadProject(final ProjectContextRequest projectContextRequest) throws BusinessException {
		return BUSINESS.loadProject(projectContextRequest);
	}
}
