package br.com.oncast.ontrack.server.services.notification;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.actionSync.ModelActionSyncEvent;

public interface NotificationService {

	void notifyActionsToOtherProjectUsers(ModelActionSyncEvent modelActionSyncEvent);

	void notifyActionToCurrentUser(ModelActionSyncEvent syncEvent);

	void notifyProjectCreation(long userId, ProjectRepresentation projectRepresentation);

	void notifyUserInformationChange(User authenticatedUser);

}
