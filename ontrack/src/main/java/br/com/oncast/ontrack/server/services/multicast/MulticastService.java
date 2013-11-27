package br.com.oncast.ontrack.server.services.multicast;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

import java.util.List;

public interface MulticastService {

	void multicastToUser(ServerPushEvent event, User user);

	void multicastToUsers(ServerPushEvent event, List<User> recipients);

	void multicastToCurrentUserClientInSpecificProject(ServerPushEvent event, UUID projectId);

	void multicastToAllUsersButCurrentUserClientInSpecificProject(ServerPushEvent event, UUID projectId);

	void multicastToAllProjectsInUserAuthorizationList(ServerPushEvent event, List<ProjectRepresentation> projectsList);

	void multicastToAllUsersInSpecificProject(ServerPushEvent event, UUID projectId);
}
