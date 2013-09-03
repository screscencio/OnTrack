package br.com.oncast.ontrack.server.services.integration;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.Profile;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface IntegrationService {

	void onUserInvited(UUID projectId, User invitor, User invitedUser, Profile profile);

	void onProjectCreated(ProjectRepresentation project, User creator);

}
