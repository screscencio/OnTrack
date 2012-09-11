package br.com.oncast.ontrack.server.services.notification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationListener;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnection;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ClientManager {

	private static final Logger LOGGER = Logger.getLogger(ClientManager.class);

	private static final UUID UNBOUND_PROJECT_INDEX = UUID.INVALID_UUID;

	private final Map<UUID, Set<ServerPushConnection>> clientsByProject = new HashMap<UUID, Set<ServerPushConnection>>();
	private final Map<String, Set<ServerPushConnection>> clientsBySession = new HashMap<String, Set<ServerPushConnection>>();

	private final UserSessionMapper userSessionMapper;

	public ClientManager(final AuthenticationManager authenticationManager) {
		userSessionMapper = new UserSessionMapper();
		authenticationManager.register(userSessionMapper);
	}

	public void bindClientToProject(final ServerPushConnection clientId, final UUID projectId) {
		if (projectId == UNBOUND_PROJECT_INDEX) throw new IllegalArgumentException("Client was not bound to the project: The given 'projectId' should not be 0");

		add(clientId, projectId, clientsByProject);
		LOGGER.debug("Client '" + clientId + "' was bound to project '" + projectId + "'.");
	}

	public void unbindClientFromProject(final ServerPushConnection clientId) {
		add(clientId, UNBOUND_PROJECT_INDEX, clientsByProject);
		LOGGER.debug("Client '" + clientId + "' was unbound from its project.");
	}

	public void registerClient(final ServerPushConnection connection) {
		add(connection, UNBOUND_PROJECT_INDEX, clientsByProject);
		add(connection, connection.getSessionId(), clientsBySession);
		LOGGER.debug("Client " + connection + " was registered.");
	}

	public void unregisterClient(final ServerPushConnection connection) {
		remove(connection, clientsByProject);
		remove(connection, clientsBySession);
		LOGGER.debug("Client " + connection + " unregistered.");
	}

	public Set<ServerPushConnection> getClientsAtProject(final UUID projectId) {
		return get(projectId, clientsByProject);
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
		final Set<ServerPushConnection> allClients = new HashSet<ServerPushConnection>();
		for (final Set<ServerPushConnection> clientIds : clientsByProject.values()) {
			allClients.addAll(clientIds);
		}
		return allClients;
	}

	private <T, E> Set<E> get(final T key, final Map<T, Set<E>> from) {
		final HashSet<E> set = new HashSet<E>();
		if (from.containsKey(key)) set.addAll(from.get(key));
		return set;
	}

	private <T, E> void add(final E value, final T key, final Map<T, Set<E>> to) {
		remove(value, to);
		if (!to.containsKey(key)) {
			to.put(key, new HashSet<E>());
		}

		to.get(key).add(value);
	}

	private <T, E> void remove(final E value, final Map<T, Set<E>> from) {
		for (final Set<E> clients : from.values()) {
			if (clients.contains(value)) {
				clients.remove(value);
				break;
			}
		}
	}

	private class UserSessionMapper implements AuthenticationListener {

		private final Map<Long, Set<String>> sessionByUser = new HashMap<Long, Set<String>>();

		private Set<String> getSessionsIdFor(final long userId) {
			return get(userId, sessionByUser);
		}

		@Override
		public void onUserLoggedIn(final User user, final String sessionId) {
			add(sessionId, user.getId(), sessionByUser);
		}

		@Override
		public void onUserLoggedOut(final User user, final String sessionId) {
			sessionByUser.get(user.getId()).remove(sessionId);
		}
	}

}
