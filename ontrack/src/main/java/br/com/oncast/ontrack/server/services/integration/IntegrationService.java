package br.com.oncast.ontrack.server.services.integration;

import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface IntegrationService {

	void onUserInvited(UUID projectId, User invitor, User invitedUser, boolean isSuperUser);

}
