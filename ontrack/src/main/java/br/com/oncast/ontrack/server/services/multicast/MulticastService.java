package br.com.oncast.ontrack.server.services.multicast;

import java.util.List;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.actionSync.ModelActionSyncEvent;
import br.com.oncast.ontrack.shared.services.notification.NotificationCreatedEvent;

public interface MulticastService {

	void notifyActionsToOtherProjectUsers(ModelActionSyncEvent modelActionSyncEvent);

	void notifyActionToCurrentUser(ModelActionSyncEvent syncEvent);

	void notifyProjectCreation(long userId, ProjectRepresentation projectRepresentation);

	void notifyUserInformationChange(User authenticatedUser);

	void multicastToUsers(NotificationCreatedEvent notificationCreatedEvent, List<User> recipients);

}
