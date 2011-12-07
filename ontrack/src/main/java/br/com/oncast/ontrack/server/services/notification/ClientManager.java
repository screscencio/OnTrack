package br.com.oncast.ontrack.server.services.notification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationListener;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ClientManager {

	private static final Logger LOGGER = Logger.getLogger(ClientManager.class);

	private static final long UNBOUND_PROJECT_INDEX = 0;

	private final Map<Long, Set<UUID>> clientsByProject = new HashMap<Long, Set<UUID>>();
	private final Map<String, Set<UUID>> clientsBySession = new HashMap<String, Set<UUID>>();

	private final UserSessionMapper userSessionMapper;

	public ClientManager(final AuthenticationManager authenticationManager) {
		userSessionMapper = new UserSessionMapper();
		authenticationManager.register(userSessionMapper);
	}

	public void bindClientToProject(final UUID clientId, final long projectId) {
		if (projectId == UNBOUND_PROJECT_INDEX) throw new IllegalArgumentException("Client was not bound to the project: The given 'projectId' should not be 0");

		LOGGER.debug("Binding client '" + clientId + "' to project '" + projectId + "'.");
		add(clientId, projectId, clientsByProject);
	}

	public void unbindClientFromProject(final UUID clientId) {
		LOGGER.debug("Unbinding client '" + clientId + "' from its project.");
		add(clientId, UNBOUND_PROJECT_INDEX, clientsByProject);
	}

	public void registerClient(final UUID clientId, final String sessionId) {
		add(clientId, UNBOUND_PROJECT_INDEX, clientsByProject);
		add(clientId, sessionId, clientsBySession);
	}

	public void unregisterClient(final UUID clientId, final String sessionId) {
		remove(clientId, clientsByProject);
	}

	public Set<UUID> getClientsAtProject(final long projectId) {
		return get(projectId, clientsByProject);
	}

	public Set<UUID> getClientsOfUser(final long userId) {
		final Set<UUID> clients = new HashSet<UUID>();
		final Set<String> sessions = userSessionMapper.getSessionsIdFor(userId);
		for (final String sessionId : sessions) {
			clients.addAll(clientsBySession.get(sessionId));
		}
		return clients;
	}

	public Set<UUID> getAllClients() {
		final Set<UUID> allClients = new HashSet<UUID>();
		for (final Set<UUID> clientIds : clientsByProject.values()) {
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
