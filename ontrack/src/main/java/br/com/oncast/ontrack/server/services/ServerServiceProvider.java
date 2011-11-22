package br.com.oncast.ontrack.server.services;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.broadcast.BroadcastService;
import br.com.oncast.ontrack.server.services.broadcast.BroadcastServiceImpl;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerServiceImpl;
import br.com.oncast.ontrack.server.services.session.SessionManager;

// TODO Review the use of this as a singleton. It is not entirely wrong to do this, but it should only be accessed directly on application entry points such as
// Servlets. In other words, its singleton access should not be used indiscriminately.
// FIXME Hide infra services, the application should only be able to use business services.
public class ServerServiceProvider {

	private AuthenticationManager authenticationManager;
	private PersistenceService persistenceService;
	private BroadcastService broadcastService;
	private ServerPushServerService serverPushServerService;
	private SessionManager sessionManager;

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
			return authenticationManager = new AuthenticationManager(getPersistenceService(), getSessionManager());
		}
	}

	public PersistenceService getPersistenceService() {
		if (persistenceService != null) return persistenceService;
		return persistenceService = new PersistenceServiceJpaImpl();
	}

	public BroadcastService getBroadcastService() {
		if (broadcastService != null) return broadcastService;
		return broadcastService = new BroadcastServiceImpl(getServerPushServerService());
	}

	public SessionManager getSessionManager() {
		if (sessionManager != null) return sessionManager;
		return sessionManager = new SessionManager();
	}

	private ServerPushServerService getServerPushServerService() {
		if (serverPushServerService != null) return serverPushServerService;
		return serverPushServerService = new ServerPushServerServiceImpl();
	}
}
