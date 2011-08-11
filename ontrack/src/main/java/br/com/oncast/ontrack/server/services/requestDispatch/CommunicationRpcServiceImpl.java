package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.oncast.ontrack.client.services.requestDispatch.CommunicationRpcService;
import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.ServerBusinessLogicLocator;
import br.com.oncast.ontrack.shared.exceptions.business.BusinessException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CommunicationRpcServiceImpl extends RemoteServiceServlet implements CommunicationRpcService {

	private static final long serialVersionUID = 1L;

	private final BusinessLogic business = ServerBusinessLogicLocator.getInstance().getBusinessLogic();

	@Override
	public void transmitAction(final ModelAction action) throws BusinessException {
		// TODO Remover SYSO
		System.out.println("Action received: " + action.getClass().getSimpleName() + " for " + action.getReferenceId());
		business.handleIncomingAction(action);
	}

	@Override
	public Project loadProject() throws BusinessException {
		// TODO Remover SYSO
		System.out.println("Loading project...");
		return business.loadProject();
	}
}
