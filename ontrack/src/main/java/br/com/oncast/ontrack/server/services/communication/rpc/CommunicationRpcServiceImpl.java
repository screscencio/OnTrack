package br.com.oncast.ontrack.server.services.communication.rpc;

import br.com.oncast.ontrack.client.services.communication.rpc.CommunicationRpcService;
import br.com.oncast.ontrack.server.mocks.ProjectMockFactory;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CommunicationRpcServiceImpl extends RemoteServiceServlet implements CommunicationRpcService {

	private static final long serialVersionUID = 1L;

	private Project projectMock;

	@Override
	public void transmitAction(final ModelAction action, final boolean isRollback) {
		System.out.println("Action received: " + (isRollback ? "Rolledback " : "Executed ") + action.getClass().getSimpleName() + " for "
				+ action.getReferenceId());
	}

	@Override
	public Project loadProject() {
		System.out.println("Loading project...");
		projectMock = ProjectMockFactory.createProjectMock();
		return projectMock;
	}
}
