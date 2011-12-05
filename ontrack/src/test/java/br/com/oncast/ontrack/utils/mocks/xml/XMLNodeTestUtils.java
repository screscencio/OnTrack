package br.com.oncast.ontrack.utils.mocks.xml;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectAuthorizationXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.UserXMLNode;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorizationEntity;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class XMLNodeTestUtils {

	public static UserXMLNode createUserNode(final User user) {
		return new UserXMLNode(user);
	}

	public static ProjectXMLNode createProjectNode(final ProjectRepresentation projectRepresentation) {
		return new ProjectXMLNode(projectRepresentation, new ArrayList<UserAction>());
	}

	public static ProjectAuthorizationXMLNode createProjectAuthorizationNode(final User user, final ProjectRepresentation project) {
		return new ProjectAuthorizationXMLNode(new ProjectAuthorizationEntity(user, project));
	}

	public static List<UserXMLNode> createUserNodes(final int size) throws Exception {
		final List<UserXMLNode> userNodes = new ArrayList<UserXMLNode>();
		for (int i = 1; i <= size; i++) {
			userNodes.add(XMLNodeTestUtils.createUserNode(UserTestUtils.createUser(i)));
		}
		return userNodes;
	}

	public static List<ProjectXMLNode> createProjectNodes(final int size) {
		final List<ProjectXMLNode> projects = new ArrayList<ProjectXMLNode>();
		for (int i = 1; i <= size; i++) {
			final ProjectXMLNode project = createProjectNode(ProjectTestUtils.createRepresentation(i));
			projects.add(project);
		}
		return projects;
	}

	public static List<ProjectAuthorizationXMLNode> createAuthorizationNodes(final List<ProjectXMLNode> projectNodes, final List<UserXMLNode> userNodes) {
		if (userNodes.size() < 3) throw new RuntimeException("This utility method needs a UserXMLNode with a size of, at least, 3.");
		if (projectNodes.size() < 2) throw new RuntimeException("This utility method needs a ProjectXMLNode with a size of, at least, 2.");

		final List<ProjectAuthorizationXMLNode> authNode = new ArrayList<ProjectAuthorizationXMLNode>();
		authNode.add(createProjectAuthorizationNode(extractUser(userNodes.get(0)), projectNodes.get(0).getProjectRepresentation()));
		authNode.add(createProjectAuthorizationNode(extractUser(userNodes.get(1)), projectNodes.get(0).getProjectRepresentation()));
		authNode.add(createProjectAuthorizationNode(extractUser(userNodes.get(0)), projectNodes.get(1).getProjectRepresentation()));
		authNode.add(createProjectAuthorizationNode(extractUser(userNodes.get(2)), projectNodes.get(1).getProjectRepresentation()));

		return authNode;
	}

	private static User extractUser(final UserXMLNode userNode) {
		final User user = userNode.getUser();
		user.setId(userNode.getId());
		return user;
	}
}
