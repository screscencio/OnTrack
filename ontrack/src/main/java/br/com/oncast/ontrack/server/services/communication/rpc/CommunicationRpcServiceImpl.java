package br.com.oncast.ontrack.server.services.communication.rpc;

import br.com.oncast.ontrack.client.services.communication.rpc.CommunicationRpcService;
import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.exception.BusinessException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CommunicationRpcServiceImpl extends RemoteServiceServlet implements CommunicationRpcService {

	private static final long serialVersionUID = 1L;

	private final BusinessLogic business = new BusinessLogic();

	@Override
	public void transmitAction(final ModelAction action) {
		// TODO Remover SYSO
		System.out.println("Action received: " + action.getClass().getSimpleName() + " for " + action.getReferenceId());
		try {
			business.handleIncomingAction(action);
		}
		catch (final BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Project loadProject() {
		// TODO Remover SYSO
		System.out.println("Loading project...");
		try {
			return business.loadProject();
		}
		catch (final BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
