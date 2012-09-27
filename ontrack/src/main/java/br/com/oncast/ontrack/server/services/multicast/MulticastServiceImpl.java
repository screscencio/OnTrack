package br.com.oncast.ontrack.server.services.multicast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnection;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnectionListener;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.server.utils.PrettyPrinter;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.actionSync.ModelActionSyncEvent;
import br.com.oncast.ontrack.shared.services.authentication.UserInformationChangeEvent;
import br.com.oncast.ontrack.shared.services.context.ProjectCreatedEvent;
import br.com.oncast.ontrack.shared.services.notification.NotificationCreatedEvent;

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
	public void notifyActionsToOtherProjectUsers(final ModelActionSyncEvent event) {
		final Set<ServerPushConnection> connectionSet = clientManager.getClientsAtProject(event.getProjectId());
		connectionSet.remove(sessionManager.getCurrentSession().getThreadLocalClientId());

		LOGGER.debug("Multicasting " + PrettyPrinter.getSimpleNamesListString(event.getActionList()) + " to project '" + event.getProjectId()
				+ "': " + connectionSet.toString() + ".");
		serverPushServerService.pushEvent(event, connectionSet);
	}

	@Override
	public void notifyActionToCurrentUser(final ModelActionSyncEvent event) {
		final ServerPushConnection localClientId = sessionManager.getCurrentSession().getThreadLocalClientId();

		if (localClientId == null || !clientManager.getClientsAtProject(event.getProjectId()).contains(localClientId)) return;

		final Set<ServerPushConnection> connectionSet = new HashSet<ServerPushConnection>();
		connectionSet.add(localClientId);

		LOGGER.debug("Multicasting " + PrettyPrinter.getSimpleNamesListString(event.getActionList()) + " to current user (" + connectionSet.toString() + ").");
		serverPushServerService.pushEvent(event, connectionSet);
	}

	@Override
	public void notifyProjectCreation(final String userEmail, final ProjectRepresentation projectRepresentation) {
		final Set<ServerPushConnection> connectionSet = clientManager.getClientsOfUser(userEmail);

		LOGGER.debug("Multicasting project creation with name '" + projectRepresentation.getName()
				+ "' to '" + connectionSet.toString() + "'.");
		serverPushServerService.pushEvent(new ProjectCreatedEvent(projectRepresentation), connectionSet);
	}

	@Override
	public void notifyUserInformationChange(final User authenticatedUser) {
		final Set<ServerPushConnection> connectionSet = clientManager.getClientsOfUser(authenticatedUser.getEmail());

		LOGGER.debug("Multicasting information change for " + User.class.getSimpleName() + " '" + authenticatedUser.getEmail()
				+ "' to " + connectionSet.toString() + ".");
		serverPushServerService.pushEvent(new UserInformationChangeEvent(authenticatedUser), connectionSet);
	}

	@Override
	public void multicastToUsers(final NotificationCreatedEvent notificationCreatedEvent, final List<User> recipients) {
		// FIXME Notification
	}
}
