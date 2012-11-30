package br.com.oncast.ontrack.server.services.multicast;

import java.util.List;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;
import br.com.oncast.ontrack.shared.services.user.UserDataUpdateEvent;

public interface MulticastService {

	void multicastToUser(ServerPushEvent event, User authenticatedUser);

	void multicastToUsers(ServerPushEvent event, List<User> recipients);

	void multicastToCurrentUserClientInSpecificProject(ServerPushEvent event, UUID projectId);

	void multicastToAllUsersButCurrentUserClientInSpecificProject(ServerPushEvent event, UUID projectId);

	void multicastToAllProjectsInUserAuthorizationList(UserDataUpdateEvent event, List<ProjectRepresentation> projectsList);

	void multicastToAllUsersInSpecificProject(ServerPushEvent event, UUID projectId);
}
