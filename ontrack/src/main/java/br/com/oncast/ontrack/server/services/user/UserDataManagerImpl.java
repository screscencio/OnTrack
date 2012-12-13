package br.com.oncast.ontrack.server.services.user;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.services.authorization.AuthorizationManager;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
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

			final List<ProjectRepresentation> listAuthorizedProjects = authorizationManager.listAuthorizedProjects(user.getId());

			multicastService.multicastToAllProjectsInUserAuthorizationList(new UserDataUpdateEvent(user), listAuthorizedProjects);

			return u;
		}
		catch (final NoResultFoundException e) {
			// throws another exception or just log?
		}
		return null;
	}

	@Override
	public List<User> retrieveUsers(final List<UUID> usersIds) {
		final List<User> userList = new ArrayList<User>();
		for (final UUID userId : usersIds) {
			try {
				userList.add(persistenceService.retrieveUserById(userId));
			}
			catch (final NoResultFoundException e) {
				e.printStackTrace();
			}
			catch (final PersistenceException e) {
				e.printStackTrace();
			}
		}
		return userList;
	}

	@Override
	public User retrieveUser(final UUID userId) throws NoResultFoundException, PersistenceException {
		return persistenceService.retrieveUserById(userId);
	}
}
