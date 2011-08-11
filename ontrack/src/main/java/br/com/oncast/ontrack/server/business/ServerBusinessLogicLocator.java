package br.com.oncast.ontrack.server.business;

import br.com.oncast.ontrack.server.services.actionSync.ActionBroadcastService;
import br.com.oncast.ontrack.server.services.actionSync.ActionBroadcastServiceImpl;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerServiceImpl;

public class ServerBusinessLogicLocator {

	private static ServerBusinessLogicLocator instance;

	private BusinessLogic businessLogic;
	private PersistenceService persistenceService;
	private ActionBroadcastService actionBroadcastService;
	private ServerPushServerService serverPushServerService;

	public static ServerBusinessLogicLocator getInstance() {
		if (instance != null) return instance;
		return instance = new ServerBusinessLogicLocator();
	}

	private ServerBusinessLogicLocator() {}

	public BusinessLogic getBusinessLogic() {
		if (businessLogic != null) return businessLogic;
		return businessLogic = new BusinessLogic(getPersistenceService(), getActionBroadcastService());
	}

	private PersistenceService getPersistenceService() {
		if (persistenceService != null) return persistenceService;
		return persistenceService = new PersistenceServiceJpaImpl();
	}

	private ActionBroadcastService getActionBroadcastService() {
		if (actionBroadcastService != null) return actionBroadcastService;
		return actionBroadcastService = new ActionBroadcastServiceImpl(getServerPushServerService());
	}

	private ServerPushServerService getServerPushServerService() {
		if (serverPushServerService != null) return serverPushServerService;
		return serverPushServerService = new ServerPushServerServiceImpl();
	}
}