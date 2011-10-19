package br.com.oncast.ontrack.server.services;

import br.com.oncast.ontrack.server.services.actionBroadcast.ActionBroadcastService;
import br.com.oncast.ontrack.server.services.actionBroadcast.ActionBroadcastServiceImpl;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerServiceImpl;

public class ServerServiceProvider {

	private AuthenticationManager authenticationManager;
	private PersistenceService persistenceService;
	private ActionBroadcastService actionBroadcastService;
	private ServerPushServerService serverPushServerService;

	private static ServerServiceProvider instance;

	public static ServerServiceProvider getInstance() {
		if (instance != null) return instance;
		return instance = new ServerServiceProvider();
	}

	private ServerServiceProvider() {}

	public AuthenticationManager getAuthenticationManager() {
		if (authenticationManager != null) return authenticationManager;
		synchronized (this) {
			if (authenticationManager != null) return authenticationManager;
			return authenticationManager = new AuthenticationManager(getPersistenceService());
		}
	}

	public PersistenceService getPersistenceService() {
		if (persistenceService != null) return persistenceService;
		return persistenceService = new PersistenceServiceJpaImpl();
	}

	public ActionBroadcastService getActionBroadcastService() {
		if (actionBroadcastService != null) return actionBroadcastService;
		return actionBroadcastService = new ActionBroadcastServiceImpl(getServerPushServerService());
	}

	private ServerPushServerService getServerPushServerService() {
		if (serverPushServerService != null) return serverPushServerService;
		return serverPushServerService = new ServerPushServerServiceImpl();
	}
}
