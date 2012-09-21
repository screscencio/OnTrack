package br.com.oncast.ontrack.server.services.multicast;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationListener;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnection;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

public class ClientManager {

	private static final Logger LOGGER = Logger.getLogger(ClientManager.class);

	private static final UUID UNBOUND_PROJECT_INDEX = UUID.INVALID_UUID;

	private final SetMultimap<UUID, ServerPushConnection> clientsByProject = HashMultimap.create();
	private final SetMultimap<String, ServerPushConnection> clientsBySession = HashMultimap.create();

	private final UserSessionMapper userSessionMapper;

	public ClientManager(final AuthenticationManager authenticationManager) {
		userSessionMapper = new UserSessionMapper();
		authenticationManager.register(userSessionMapper);
	}

	public void bindClientToProject(final ServerPushConnection clientId, final UUID projectId) {
		if (projectId == UNBOUND_PROJECT_INDEX) throw new IllegalArgumentException("Client was not bound to the project: The given 'projectId' should not be 0");

		removeAllValuesInPlace(clientsByProject, clientId);

		clientsByProject.put(projectId, clientId);
		LOGGER.debug("Client '" + clientId + "' was bound to project '" + projectId + "'.");
	}

	public void unbindClientFromProject(final ServerPushConnection clientId) {
		removeAllValuesInPlace(clientsByProject, clientId);
		clientsByProject.put(UNBOUND_PROJECT_INDEX, clientId);
		LOGGER.debug("Client '" + clientId + "' was unbound from its project.");
	}

	public void registerClient(final ServerPushConnection connection) {
		clientsByProject.put(UNBOUND_PROJECT_INDEX, connection);
		clientsBySession.put(connection.getSessionId(), connection);
		LOGGER.debug("Client " + connection + " was registered.");
	}

	public void unregisterClient(final ServerPushConnection connection) {
		removeAllValuesInPlace(clientsByProject, connection);
		removeAllValuesInPlace(clientsBySession, connection);
		LOGGER.debug("Client " + connection + " unregistered.");
	}

	public Set<ServerPushConnection> getClientsAtProject(final UUID projectId) {
		return Sets.newHashSet(clientsByProject.get(projectId));
	}

	public Set<ServerPushConnection> getClientsOfUser(final long userId) {
		final Set<ServerPushConnection> clients = new HashSet<ServerPushConnection>();
		final Set<String> sessions = userSessionMapper.getSessionsIdFor(userId);
		for (final String sessionId : sessions) {
			clients.addAll(clientsBySession.get(sessionId));
		}
		return clients;
	}

	public Set<ServerPushConnection> getAllClients() {
		return Sets.newHashSet(clientsByProject.values());
	}

	private void removeAllValuesInPlace(final SetMultimap<?, ServerPushConnection> multimap, final ServerPushConnection clientId) {
		final Collection<ServerPushConnection> values = multimap.values();
		while (values.remove(clientId))
			;
	}

	private class UserSessionMapper implements AuthenticationListener {

		private final SetMultimap<Long, String> sessionByUser = HashMultimap.create();

		private Set<String> getSessionsIdFor(final long userId) {
			return sessionByUser.get(userId);
		}

		@Override
		public void onUserLoggedIn(final User user, final String sessionId) {
			sessionByUser.put(user.getId(), sessionId);
		}

		@Override
		public void onUserLoggedOut(final User user, final String sessionId) {
			sessionByUser.remove(user.getId(), sessionId);
		}
	}

}
