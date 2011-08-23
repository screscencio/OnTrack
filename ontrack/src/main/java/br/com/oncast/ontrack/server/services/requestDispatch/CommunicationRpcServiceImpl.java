package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.oncast.ontrack.client.services.requestDispatch.CommunicationRpcService;
import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.ServerBusinessLogicLocator;
import br.com.oncast.ontrack.shared.exceptions.business.BusinessException;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CommunicationRpcServiceImpl extends RemoteServiceServlet implements CommunicationRpcService {

	private static final long serialVersionUID = 1L;

	private final BusinessLogic business = ServerBusinessLogicLocator.getInstance().getBusinessLogic();

	@Override
	public void transmitAction(final ModelActionSyncRequest modelActionSyncRequest) throws BusinessException {
		business.handleIncomingActionSyncRequest(modelActionSyncRequest);
	}

	@Override
	public Project loadProject() throws BusinessException {
		return business.loadProject();
	}
}
