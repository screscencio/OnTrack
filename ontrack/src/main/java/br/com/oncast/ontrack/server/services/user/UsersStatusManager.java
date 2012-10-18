package br.com.oncast.ontrack.server.services.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.authorization.AuthorizationManager;
import br.com.oncast.ontrack.server.services.multicast.ClientManager;
import br.com.oncast.ontrack.server.services.multicast.ClientManager.UserStatusChangeListener;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnection;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.user.UserClosedProjectEvent;
import br.com.oncast.ontrack.shared.services.user.UserOfflineEvent;
import br.com.oncast.ontrack.shared.services.user.UserOnlineEvent;
import br.com.oncast.ontrack.shared.services.user.UserOpenProjectEvent;
import br.com.oncast.ontrack.shared.services.user.UserSelectedScopeEvent;
import br.com.oncast.ontrack.shared.services.user.UserStatusEvent;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class UsersStatusManager implements UserStatusChangeListener {

	private static final Logger LOGGER = Logger.getLogger(UsersStatusManager.class);

	private final ClientManager clientManager;
	private final MulticastService multicastService;
	private final ListMultimap<UUID, UUID> openedProjectsRegistry;
	private final List<UUID> onlineUsersRegistry;
	private final AuthorizationManager authorizationManager;

	public UsersStatusManager(final ClientManager clientManager, final MulticastService multicastService, final AuthorizationManager authorizationManager) {
		this.clientManager = clientManager;
		this.multicastService = multicastService;
		this.authorizationManager = authorizationManager;

		openedProjectsRegistry = ArrayListMultimap.create();
		onlineUsersRegistry = new ArrayList<UUID>();

		clientManager.addUserStatusChangeListener(this);
	}

	public Set<UUID> getOnlineUsers(final UUID projectId) {
		final Set<UUID> allOnlineUsers = clientManager.getOnlineUsers();
		final Set<UUID> onlineUsers = new HashSet<UUID>();
		for (final UUID userId : allOnlineUsers) {
			try {
				if (authorizationManager.hasAuthorizationFor(userId, projectId)) onlineUsers.add(userId);
			}
			catch (final NoResultFoundException e) {
				LOGGER.error("getOnlineUsers failed", e);
			}
			catch (final PersistenceException e) {
				LOGGER.error("getOnlineUsers failed", e);
			}
		}
		return onlineUsers;
	}

	public Set<UUID> getUsersAtProject(final UUID projectId) {
		return clientManager.getUsersAtProject(projectId);
	}

	@Override
	public void onUserOpenProject(final UUID projectId, final UUID userId) {
		if (!openedProjectsRegistry.containsEntry(projectId, userId)) multicastForSpecificProject(projectId, new UserOpenProjectEvent(userId));

		openedProjectsRegistry.put(projectId, userId);
	}

	@Override
	public void onUserCloseProject(final UUID projectId, final UUID userId) {
		if (openedProjectsRegistry.remove(projectId, userId) && !openedProjectsRegistry.containsEntry(projectId, userId)) multicastForSpecificProject(
				projectId,
				new UserClosedProjectEvent(userId));
	}

	@Override
	public void onUserOnline(final UUID userId) {
		if (!onlineUsersRegistry.contains(userId)) multicastForAuthorizedProjects(new UserOnlineEvent(userId));
		onlineUsersRegistry.add(userId);
	}

	@Override
	public void onUserOffline(final UUID userId) {
		if (onlineUsersRegistry.remove(userId) && !onlineUsersRegistry.contains(userId)) multicastForAuthorizedProjects(new UserOfflineEvent(userId));
	}

	private void multicastForSpecificProject(final UUID projectId, final UserStatusEvent event) {
		multicastService.multicastToAllUsersButCurrentUserClientInSpecificProject(event, projectId);
	}

	private void multicastForAuthorizedProjects(final UserStatusEvent event) {
		try {
			for (final ProjectRepresentation representation : authorizationManager.listAuthorizedProjects(event.getUserId())) {
				multicastService.multicastToAllUsersButCurrentUserClientInSpecificProject(event, representation.getId());
			}
		}
		catch (final PersistenceException e) {
			LOGGER.error("Multicast of " + event.getClass().getSimpleName() + " failed", e);
		}
		catch (final NoResultFoundException e) {
			LOGGER.error("Multicast of " + event.getClass().getSimpleName() + " failed", e);
		}
	}

	public void onUserSelectedScope(final UUID userId, final ServerPushConnection clientId, final UUID selectedScopeId) {
		final UUID projectId = clientManager.getCurrentProject(clientId);
		multicastService.multicastToAllUsersButCurrentUserClientInSpecificProject(new UserSelectedScopeEvent(userId, selectedScopeId), projectId);
	}
}
