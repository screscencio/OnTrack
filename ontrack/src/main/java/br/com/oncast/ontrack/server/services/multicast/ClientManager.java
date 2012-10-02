package br.com.oncast.ontrack.server.services.multicast;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
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

	public Set<ServerPushConnection> getClientsOfUser(final String email) {
		final Set<ServerPushConnection> clients = new HashSet<ServerPushConnection>();
		final Set<String> sessions = userSessionMapper.getSessionsIdFor(email);
		for (final String sessionId : sessions) {
			clients.addAll(clientsBySession.get(sessionId));
		}
		return clients;
	}

	public Set<ServerPushConnection> getAllClients() {
		return Sets.newHashSet(clientsByProject.values());
	}

	private void notifyOpenedProject(final ServerPushConnection clientId, final UUID projectId) {
		final String userEmail = userSessionMapper.getUserEmailFor(clientId.getSessionId());
		for (final UserStatusChangeListener l : listeners) {
			l.onUserOpenProject(projectId, userEmail);
		}
	}

	private void notifyClosedProject(final ServerPushConnection clientId, final UUID previousProjectId) {
		if (previousProjectId == null || previousProjectId.equals(UNBOUND_PROJECT_INDEX)) return;

		final String userEmail = userSessionMapper.getUserEmailFor(clientId.getSessionId());
		for (final UserStatusChangeListener l : listeners) {
			l.onUserCloseProject(previousProjectId, userEmail);
		}
	}

	private void removeFromPreviousProject(final ServerPushConnection clientId) {
		final UUID previousProjectId = getKeyFor(clientsByProject, clientId);
		removeAllValuesInPlace(clientsByProject, clientId);

		notifyClosedProject(clientId, previousProjectId);
	}

	private void removeAllValuesInPlace(final SetMultimap<?, ServerPushConnection> multimap, final ServerPushConnection clientId) {
		final Collection<ServerPushConnection> values = multimap.values();
		while (values.remove(clientId))
			;
	}

	private <K, V> K getKeyFor(final SetMultimap<K, V> multimap, final V value) {
		for (final Entry<K, V> e : multimap.entries()) {
			if (e.getValue().equals(value)) return e.getKey();
		}
		return null;
	}

	private class UserSessionMapper implements AuthenticationListener {

		private final SetMultimap<String, String> sessionByUser = HashMultimap.create();

		private Set<String> getSessionsIdFor(final String userEmail) {
			return sessionByUser.get(userEmail);
		}

		public String getUserEmailFor(final String sessionId) {
			return getKeyFor(sessionByUser, sessionId);
		}

		@Override
		public void onUserLoggedIn(final User user, final String sessionId) {
			sessionByUser.put(user.getEmail(), sessionId);

			verifyAndNotifyUserOnline(user.getEmail(), sessionId);
		}

		private void verifyAndNotifyUserOnline(final String userEmail, final String sessionId) {
			if (!clientsBySession.containsKey(sessionId)) return;

			for (final UserStatusChangeListener l : listeners) {
				l.onUserOnline(userEmail);
			}
		}

		@Override
		public void onUserLoggedOut(final User user, final String sessionId) {
			sessionByUser.remove(user.getEmail(), sessionId);

			verifyAndNotifyUserOffline(user.getEmail(), sessionId);
		}

		private void verifyAndNotifyUserOffline(final String userEmail, final String sessionId) {
			if (!clientsBySession.containsKey(sessionId)) return;

			for (final UserStatusChangeListener l : listeners) {
				l.onUserOffline(userEmail);
			}
		}

		public Set<String> getOnlineUsers() {
			final Set<String> onlineUsers = new HashSet<String>();

			for (final String user : sessionByUser.keySet()) {
				for (final String activeSession : sessionByUser.get(user)) {
					if (clientsBySession.containsKey(activeSession)) {
						onlineUsers.add(user);
					}
				}
			}
			return onlineUsers;
		}

		public Set<String> getUsers(final Set<ServerPushConnection> clients) {
			final Set<String> users = new HashSet<String>();

			for (final ServerPushConnection c : clients) {
				for (final String user : sessionByUser.keySet()) {
					if (sessionByUser.containsEntry(user, c.getSessionId())) {
						users.add(user);
						break;
					}
				}
			}
			return users;
		}
	}

	public Set<String> getOnlineUsers() {
		return userSessionMapper.getOnlineUsers();
	}

	public Set<String> getUsersAtProject(final UUID projectId) {
		return userSessionMapper.getUsers(getClientsAtProject(projectId));
	}

	public void addUserStatusChangeListener(final UserStatusChangeListener listener) {
		listeners.add(listener);
	}

	public interface UserStatusChangeListener {
		void onUserOpenProject(UUID projectId, String userEmail);

		void onUserCloseProject(UUID projectId, String userEmail);

		void onUserOnline(String userEmail);

		void onUserOffline(String userEmail);
	}

	private void verifyAndNotifyUserOnline(final ServerPushConnection clientId) {
		final String userEmail = userSessionMapper.getUserEmailFor(clientId.getSessionId());
		if (userEmail == null) return;

		notifyUserOnline(userEmail);
	}

	private void notifyUserOnline(final String userEmail) {
		for (final UserStatusChangeListener l : listeners) {
			l.onUserOnline(userEmail);
		}
	}

	private void verifyAndNotifyUserOffline(final ServerPushConnection clientId) {
		final String userEmail = userSessionMapper.getUserEmailFor(clientId.getSessionId());
		if (userEmail == null) return;

		notifyUserOffline(userEmail);
	}

	private void notifyUserOffline(final String userEmail) {
		for (final UserStatusChangeListener l : listeners) {
			l.onUserOffline(userEmail);
		}
	}

}
