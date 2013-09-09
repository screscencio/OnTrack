package br.com.oncast.ontrack.server.services.multicast;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationListener;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnection;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

public class ClientManager {

	private static final Logger LOGGER = Logger.getLogger(ClientManager.class);

	private static final UUID UNBOUND_PROJECT_INDEX = UUID.INVALID_UUID;

	private final SetMultimap<UUID, ServerPushConnection> clientsByProject = HashMultimap.create();
	private final SetMultimap<String, ServerPushConnection> clientsBySession = HashMultimap.create();

	private final UserSessionMapper userSessionMapper;

	private final Set<UserStatusChangeListener> listeners;

	public ClientManager(final AuthenticationManager authenticationManager) {
		listeners = new HashSet<UserStatusChangeListener>();
		userSessionMapper = new UserSessionMapper();
		authenticationManager.register(userSessionMapper);
	}

	public void bindClientToProject(final ServerPushConnection clientId, final UUID projectId) {
		if (projectId == UNBOUND_PROJECT_INDEX) throw new IllegalArgumentException("Client was not bound to the project: The given 'projectId' should not be 0");
		removeFromPreviousProject(clientId);

		clientsByProject.put(projectId, clientId);
		notifyOpenedProject(clientId, projectId);

		LOGGER.debug("Client '" + clientId + "' was bound to project '" + projectId + "'.");
	}

	public void unbindClientFromProject(final ServerPushConnection clientId) {
		removeFromPreviousProject(clientId);
		clientsByProject.put(UNBOUND_PROJECT_INDEX, clientId);

		LOGGER.debug("Client '" + clientId + "' was unbound from its project.");
	}

	public void registerClient(final ServerPushConnection connection) {
		clientsByProject.put(UNBOUND_PROJECT_INDEX, connection);
		clientsBySession.put(connection.getSessionId(), connection);

		verifyAndNotifyUserOnline(connection);

		LOGGER.debug("Client " + connection + " was registered.");
	}

	public void unregisterClient(final ServerPushConnection connection) {
		removeFromPreviousProject(connection);
		removeAllValuesInPlace(clientsBySession, connection);

		verifyAndNotifyUserOffline(connection);

		LOGGER.debug("Client " + connection + " unregistered.");
	}

	public Set<ServerPushConnection> getClientsAtProject(final UUID projectId) {
		return Sets.newHashSet(clientsByProject.get(projectId));
	}

	public Set<ServerPushConnection> getClientsOfUser(final UUID userId) {
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

	private void notifyOpenedProject(final ServerPushConnection clientId, final UUID projectId) {
		final UUID userId = userSessionMapper.getUserIdFor(clientId.getSessionId());
		for (final UserStatusChangeListener l : listeners) {
			l.onUserOpenProject(projectId, userId);
		}
	}

	private void notifyClosedProject(final ServerPushConnection clientId, final UUID previousProjectId) {
		if (previousProjectId == null || previousProjectId.equals(UNBOUND_PROJECT_INDEX)) return;

		final UUID userId = userSessionMapper.getUserIdFor(clientId.getSessionId());
		for (final UserStatusChangeListener l : listeners) {
			l.onUserCloseProject(previousProjectId, userId);
		}
	}

	private void removeFromPreviousProject(final ServerPushConnection clientId) {
		final UUID previousProjectId = getKeyFor(clientsByProject, clientId);
		removeAllValuesInPlace(clientsByProject, clientId);

		notifyClosedProject(clientId, previousProjectId);
	}

	private void removeAllValuesInPlace(final SetMultimap<?, ServerPushConnection> multimap, final ServerPushConnection clientId) {
		final Collection<ServerPushConnection> values = multimap.values();
		while (values.remove(clientId));
	}

	private <K, V> K getKeyFor(final SetMultimap<K, V> multimap, final V value) {
		for (final Entry<K, V> e : multimap.entries()) {
			if (e.getValue().equals(value)) return e.getKey();
		}
		return null;
	}

	private class UserSessionMapper implements AuthenticationListener {

		private final SetMultimap<UUID, String> sessionByUser = HashMultimap.create();

		private Set<String> getSessionsIdFor(final UUID userId) {
			return sessionByUser.get(userId);
		}

		public UUID getUserIdFor(final String sessionId) {
			return getKeyFor(sessionByUser, sessionId);
		}

		@Override
		public void onUserLoggedIn(final User user, final String sessionId) {
			sessionByUser.put(user.getId(), sessionId);

			verifyAndNotifyUserOnline(user.getId(), sessionId);
		}

		private void verifyAndNotifyUserOnline(final UUID userId, final String sessionId) {
			if (!clientsBySession.containsKey(sessionId)) return;

			notifyUserOnline(userId);
		}

		@Override
		public void onUserLoggedOut(final User user, final String sessionId) {
			for (final ServerPushConnection c : clientsBySession.get(sessionId)) {
				unbindClientFromProject(c);
			}

			sessionByUser.remove(user.getId(), sessionId);

			verifyAndNotifyUserOffline(user.getId(), sessionId);
		}

		private void verifyAndNotifyUserOffline(final UUID userId, final String sessionId) {
			if (!clientsBySession.containsKey(sessionId)) return;

			notifyUserOffline(userId);
		}

		public Set<UUID> getOnlineUsers() {
			final Set<UUID> onlineUsers = new HashSet<UUID>();

			for (final UUID user : sessionByUser.keySet()) {
				for (final String activeSession : sessionByUser.get(user)) {
					if (clientsBySession.containsKey(activeSession)) {
						onlineUsers.add(user);
					}
				}
			}
			return onlineUsers;
		}

		public Set<UUID> getUsers(final Set<ServerPushConnection> clients) {
			final Set<UUID> users = new HashSet<UUID>();

			for (final ServerPushConnection c : clients) {
				for (final UUID user : sessionByUser.keySet()) {
					if (sessionByUser.containsEntry(user, c.getSessionId())) {
						users.add(user);
						break;
					}
				}
			}
			return users;
		}
	}

	public Set<UUID> getOnlineUsers() {
		return userSessionMapper.getOnlineUsers();
	}

	public Set<UUID> getUsersAtProject(final UUID projectId) {
		return userSessionMapper.getUsers(getClientsAtProject(projectId));
	}

	public void addUserStatusChangeListener(final UserStatusChangeListener listener) {
		listeners.add(listener);
	}

	public interface UserStatusChangeListener {
		void onUserOpenProject(UUID projectId, UUID userId);

		void onUserCloseProject(UUID projectId, UUID userId);

		void onUserOnline(UUID userId);

		void onUserOffline(UUID userId);
	}

	private void verifyAndNotifyUserOnline(final ServerPushConnection clientId) {
		final UUID userId = userSessionMapper.getUserIdFor(clientId.getSessionId());
		if (userId == null) return;

		notifyUserOnline(userId);
	}

	private void notifyUserOnline(final UUID userId) {
		for (final UserStatusChangeListener l : listeners) {
			l.onUserOnline(userId);
		}
	}

	private void verifyAndNotifyUserOffline(final ServerPushConnection clientId) {
		final UUID userId = userSessionMapper.getUserIdFor(clientId.getSessionId());
		if (userId == null) return;

		notifyUserOffline(userId);
	}

	private void notifyUserOffline(final UUID userId) {
		for (final UserStatusChangeListener l : listeners) {
			l.onUserOffline(userId);
		}
	}

	public UUID getCurrentProject(final ServerPushConnection clientId) {
		return getKeyFor(clientsByProject, clientId);
	}

	public void unbindUserFromProject(final UUID userId, final UUID projectId) {
		for (final ServerPushConnection connection : getClientsOfUser(userId)) {
			if (projectId.equals(getCurrentProject(connection))) unbindClientFromProject(connection);
		}
	}
}
