package br.com.oncast.ontrack.server.services.notification;

import java.util.Set;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnectionListener;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionSync.ServerActionSyncEvent;
import br.com.oncast.ontrack.shared.services.authentication.UserInformationChangeEvent;
import br.com.oncast.ontrack.shared.services.context.ProjectCreatedEvent;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;

public class NotificationServiceImpl implements NotificationService {

	private static final Logger LOGGER = Logger.getLogger(NotificationServiceImpl.class);
	private final ServerPushServerService serverPushServerService;
	private final ClientManager clientManager;
	private final SessionManager sessionManager;

	public NotificationServiceImpl(final ServerPushServerService serverPushServerService, final ClientManager clientManager, final SessionManager sessionManager) {
		this.serverPushServerService = serverPushServerService;
		this.clientManager = clientManager;
		this.sessionManager = sessionManager;

		// TODO Maybe move this registration logic to ClientManager
		this.serverPushServerService.registerConnectionListener(new ServerPushConnectionListener() {

			@Override
			public void onClientConnected(final UUID clientId, final String sessionId) {
				clientManager.registerClient(clientId, sessionId);
			}

			@Override
			public void onClientDisconnected(final UUID clientId) {
				clientManager.unregisterClient(clientId);
			}
		});
	}

	@Override
	public void notifyActions(final ModelActionSyncRequest modelActionSyncRequest) {
		final Set<UUID> connectionSet = clientManager.getClientsAtProject(modelActionSyncRequest.getProjectId());
		connectionSet.remove(sessionManager.getCurrentSession().getThreadLocalClientId());

		LOGGER.debug("Multicasting " + ModelActionSyncRequest.class.getSimpleName() + " with projectId '" + modelActionSyncRequest.getProjectId()
				+ "' to '" + connectionSet.toString() + "'.");
		serverPushServerService.pushEvent(new ServerActionSyncEvent(modelActionSyncRequest), connectionSet);
	}

	@Override
	public void notifyProjectCreation(final long userId, final ProjectRepresentation projectRepresentation) {
		final Set<UUID> connectionSet = clientManager.getClientsOfUser(userId);

		LOGGER.debug("Multicasting " + ProjectRepresentation.class.getSimpleName() + " with name '" + projectRepresentation.getName()
				+ "' to '" + connectionSet.toArray().toString() + "'.");
		serverPushServerService.pushEvent(new ProjectCreatedEvent(projectRepresentation), connectionSet);
	}

	@Override
	public void notifyUserInformationChange(final User authenticatedUser) {
		final Set<UUID> connectionSet = clientManager.getClientsOfUser(authenticatedUser.getId());

		LOGGER.debug("Multicasting " + User.class.getSimpleName() + " of '" + authenticatedUser.getEmail()
				+ "' to '" + connectionSet.toArray().toString() + "'.");
		serverPushServerService.pushEvent(new UserInformationChangeEvent(authenticatedUser), connectionSet);
	}

}
