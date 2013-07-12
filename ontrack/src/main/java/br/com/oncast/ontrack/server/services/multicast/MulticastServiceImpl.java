package br.com.oncast.ontrack.server.services.multicast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnection;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnectionListener;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;
import br.com.oncast.ontrack.shared.utils.PrettyPrinter;

public class MulticastServiceImpl implements MulticastService {

	private static final Logger LOGGER = Logger.getLogger(MulticastServiceImpl.class);
	private final ServerPushServerService serverPushServerService;
	private final ClientManager clientManager;
	private final SessionManager sessionManager;

	public MulticastServiceImpl(final ServerPushServerService serverPushServerService, final ClientManager clientManager, final SessionManager sessionManager) {
		this.serverPushServerService = serverPushServerService;
		this.clientManager = clientManager;
		this.sessionManager = sessionManager;

		// TODO Maybe move this registration logic to ClientManager
		this.serverPushServerService.registerConnectionListener(new ServerPushConnectionListener() {
			@Override
			public void onClientConnected(final ServerPushConnection connection) {
				clientManager.registerClient(connection);
			}

			@Override
			public void onClientDisconnected(final ServerPushConnection connection) {
				clientManager.unregisterClient(connection);
			}
		});
	}

	@Override
	public void multicastToAllUsersButCurrentUserClientInSpecificProject(final ServerPushEvent event, final UUID projectId) {
		final Set<ServerPushConnection> connectionSet = clientManager.getClientsAtProject(projectId);
		connectionSet.remove(sessionManager.getCurrentSession().getThreadLocalClientId());

		LOGGER.debug("Multicasting '" + event.getClass().getSimpleName() + "' event (" + event.toString() + ") to '"
				+ PrettyPrinter.getSimpleNamesListString(connectionSet) + "'.");
		serverPushServerService.pushEvent(event, connectionSet);
	}

	@Override
	public void multicastToAllUsersInSpecificProject(final ServerPushEvent event, final UUID projectId) {
		final Set<ServerPushConnection> connectionSet = clientManager.getClientsAtProject(projectId);

		LOGGER.debug("Multicasting '" + event.getClass().getSimpleName() + "' event (" + event.toString() + ") to '"
				+ PrettyPrinter.getSimpleNamesListString(connectionSet) + "'.");
		serverPushServerService.pushEvent(event, connectionSet);
	}

	@Override
	public void multicastToCurrentUserClientInSpecificProject(final ServerPushEvent event, final UUID projectId) {
		final ServerPushConnection localClientId = sessionManager.getCurrentSession().getThreadLocalClientId();
		if (localClientId == null || !clientManager.getClientsAtProject(projectId).contains(localClientId)) return;

		final Set<ServerPushConnection> connectionSet = new HashSet<ServerPushConnection>();
		connectionSet.add(localClientId);

		LOGGER.debug("Multicasting '" + event.getClass().getSimpleName() + "' event (" + event.toString() + ") to '"
				+ PrettyPrinter.getSimpleNamesListString(connectionSet) + "'.");
		serverPushServerService.pushEvent(event, connectionSet);
	}

	@Override
	public void multicastToUser(final ServerPushEvent event, final User user) {
		final Set<ServerPushConnection> connectionSet = clientManager.getClientsOfUser(user.getId());

		LOGGER.debug("Multicasting '" + event.getClass().getSimpleName() + "' event (" + event.toString() + ") to '" + user.getId() + "'.");
		serverPushServerService.pushEvent(event, connectionSet);
	}

	@Override
	public void multicastToUsers(final ServerPushEvent event, final List<User> recipients) {
		final Set<ServerPushConnection> connectionSet = new HashSet<ServerPushConnection>();
		for (final User user : recipients) {
			final Set<ServerPushConnection> clientsOfUser = clientManager.getClientsOfUser(user.getId());
			connectionSet.addAll(clientsOfUser);
		}

		LOGGER.debug("Multicasting '" + event.getClass().getSimpleName() + "' event (" + event.toString() + ") to '"
				+ PrettyPrinter.getSimpleNamesListString(recipients) + "'.");
		serverPushServerService.pushEvent(event, connectionSet);
	}

	@Override
	public void multicastToAllProjectsInUserAuthorizationList(final ServerPushEvent event, final List<ProjectRepresentation> projectsList) {
		final Set<ServerPushConnection> connectionSet = new HashSet<ServerPushConnection>();

		for (final ProjectRepresentation projectRepresentation : projectsList)
			connectionSet.addAll(clientManager.getClientsAtProject(projectRepresentation.getId()));

		LOGGER.debug("Multicasting '" + event.getClass().getSimpleName() + "' event (" + event.toString() + ") to '"
				+ PrettyPrinter.getToStringListString(connectionSet) + "'.");
		serverPushServerService.pushEvent(event, connectionSet);

	}
}
