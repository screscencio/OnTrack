package br.com.oncast.ontrack.server.services.user;

import java.util.List;

import br.com.oncast.ontrack.server.services.authorization.AuthorizationManager;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.user.UserDataUpdateEvent;

public class UserDataManagerImpl implements UserDataManager {

	private final PersistenceService persistenceService;
	private final MulticastService multicastService;
	private final AuthorizationManager authorizationManager;

	public UserDataManagerImpl(final PersistenceService persistenceService, final MulticastService multicastService,
			final AuthorizationManager authorizationManager) {
		this.persistenceService = persistenceService;
		this.multicastService = multicastService;
		this.authorizationManager = authorizationManager;
	}

	@Override
	public User updateUserInformation(final User user) throws PersistenceException {
		try {
			final User u = persistenceService.persistOrUpdateUser(user);

			final List<ProjectRepresentation> listAuthorizedProjects = authorizationManager.listAuthorizedProjects(new UserRepresentation(user.getId()));

			multicastService.multicastToAllProjectsInUserAuthorizationList(new UserDataUpdateEvent(user), listAuthorizedProjects);

			return u;
		}
		catch (final NoResultFoundException e) {
			// throws another exception or just log?
		}
		return null;
	}

	@Override
	public List<User> findAllUsersForProjectId(final UUID projectId) {
		try {
			final ProjectRepresentation projectRepresentation = persistenceService.retrieveProjectRepresentation(projectId);
			return persistenceService.retrieveProjectUsers(projectRepresentation);
		}
		catch (final PersistenceException e) {
			// throws another exception or just log?
		}
		catch (final NoResultFoundException e) {
			// throws another exception or just log?
		}
		return null;
	}
}
