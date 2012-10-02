package br.com.oncast.ontrack.server.services.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.authorization.AuthorizationManager;
import br.com.oncast.ontrack.server.services.multicast.ClientManager;
import br.com.oncast.ontrack.server.services.multicast.ClientManager.UserStatusChangeListener;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.user.UserClosedProjectEvent;
import br.com.oncast.ontrack.shared.services.user.UserOfflineEvent;
import br.com.oncast.ontrack.shared.services.user.UserOnlineEvent;
import br.com.oncast.ontrack.shared.services.user.UserOpenProjectEvent;
import br.com.oncast.ontrack.shared.services.user.UserStatusEvent;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class UsersStatusManager implements UserStatusChangeListener {

	private static final Logger LOGGER = Logger.getLogger(UsersStatusManager.class);

	private final ClientManager clientManager;
	private final MulticastService multicastService;
	private final ListMultimap<UUID, String> openedProjectsRegistry;
	private final List<String> onlineUsersRegistry;
	private final AuthorizationManager authorizationManager;

	public UsersStatusManager(final ClientManager clientManager, final MulticastService multicastService, final AuthorizationManager authorizationManager) {
		this.clientManager = clientManager;
		this.multicastService = multicastService;
		this.authorizationManager = authorizationManager;

		openedProjectsRegistry = ArrayListMultimap.create();
		onlineUsersRegistry = new ArrayList<String>();

		clientManager.addUserStatusChangeListener(this);
	}

	public Set<String> getOnlineUsers() {
		return clientManager.getOnlineUsers();
	}

	public Set<String> getUsersAtProject(final UUID projectId) {
		return clientManager.getUsersAtProject(projectId);
	}

	@Override
	public void onUserOpenProject(final UUID projectId, final String userEmail) {
		if (!openedProjectsRegistry.containsEntry(projectId, userEmail)) multicastForSpecificProject(projectId, new UserOpenProjectEvent(userEmail));

		openedProjectsRegistry.put(projectId, userEmail);
	}

	@Override
	public void onUserCloseProject(final UUID projectId, final String userEmail) {
		if (openedProjectsRegistry.remove(projectId, userEmail) && !openedProjectsRegistry.containsEntry(projectId, userEmail)) multicastForSpecificProject(
				projectId,
				new UserClosedProjectEvent(userEmail));
	}

	@Override
	public void onUserOnline(final String userEmail) {
		if (!onlineUsersRegistry.contains(userEmail)) multicastForAuthorizedProjects(new UserOnlineEvent(userEmail));
		onlineUsersRegistry.add(userEmail);
	}

	@Override
	public void onUserOffline(final String userEmail) {
		if (onlineUsersRegistry.remove(userEmail) && !onlineUsersRegistry.contains(userEmail)) multicastForAuthorizedProjects(new UserOfflineEvent(
				userEmail));
	}

	private void multicastForSpecificProject(final UUID projectId, final UserStatusEvent event) {
		multicastService.multicastToAllUsersButCurrentUserClientInSpecificProject(event, projectId);
	}

	private void multicastForAuthorizedProjects(final UserStatusEvent event) {
		try {
			for (final ProjectRepresentation representation : authorizationManager.listAuthorizedProjects(event.getUserEmail())) {
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

}
