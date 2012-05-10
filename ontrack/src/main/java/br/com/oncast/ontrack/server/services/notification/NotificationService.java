package br.com.oncast.ontrack.server.services.notification;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

public interface NotificationService {

	void notifyActions(ModelActionSyncRequest modelActionSyncRequest);

	void notifyProjectCreation(long userId, ProjectRepresentation projectRepresentation);

	void notifyUserInformationChange(User authenticatedUser);

}
