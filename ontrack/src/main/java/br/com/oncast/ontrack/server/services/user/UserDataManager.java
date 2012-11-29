package br.com.oncast.ontrack.server.services.user;

import java.util.List;

import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface UserDataManager {

	User updateUserInformation(User user);

	List<User> findAllUsersForProjectId(UUID projectId);

}
