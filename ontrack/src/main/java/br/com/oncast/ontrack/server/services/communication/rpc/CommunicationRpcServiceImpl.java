package br.com.oncast.ontrack.server.services.communication.rpc;

import br.com.oncast.ontrack.client.services.communication.rpc.CommunicationRpcService;
import br.com.oncast.ontrack.server.mocks.ProjectMockFactory;
import br.com.oncast.ontrack.shared.project.Project;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CommunicationRpcServiceImpl extends RemoteServiceServlet implements CommunicationRpcService {

	private static final long serialVersionUID = -1859057225839408154L;
	private Project projectMock;

	@Override
	public void transmitAction(final ScopeAction action) {
		System.out.println("Action received: " + action.getScopeId() + " " + action.getClass());
	}

	@Override
	public Project loadProject() {
		System.out.println("Loading project...");
		projectMock = ProjectMockFactory.createProjectMock();
		return projectMock;
	}
}
