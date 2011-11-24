package br.com.oncast.ontrack.server.business;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.broadcast.BroadcastService;
import br.com.oncast.ontrack.server.services.broadcast.BroadcastServiceImpl;
import br.com.oncast.ontrack.server.services.exportImport.xml.XMLExporterService;
import br.com.oncast.ontrack.server.services.exportImport.xml.XMLImporterService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerServiceImpl;
import br.com.oncast.ontrack.server.services.session.SessionManager;

public class ServerServiceProvider {

	private static ServerServiceProvider instance;

	private BusinessLogic businessLogic;
	private XMLExporterService xmlExporter;
	private XMLImporterService xmlImporter;

	private AuthenticationManager authenticationManager;
	private BroadcastService broadcastService;

	private SessionManager sessionManager;
	private ServerPushServerService serverPushServerService;
	private PersistenceService persistenceService;

	public static ServerServiceProvider getInstance() {
		if (instance != null) return instance;
		return instance = new ServerServiceProvider();
	}

	private ServerServiceProvider() {}

	public BusinessLogic getBusinessLogic() {
		if (businessLogic != null) return businessLogic;
		synchronized (this) {
			if (businessLogic != null) return businessLogic;
			return businessLogic = new BusinessLogicImpl(getPersistenceService(), getBroadcastService());
		}
	}

	// XXX Auth; Think about this method visibility.
	public AuthenticationManager getAuthenticationManager() {
		if (authenticationManager != null) return authenticationManager;
		synchronized (this) {
			if (authenticationManager != null) return authenticationManager;
			return authenticationManager = new AuthenticationManager(getPersistenceService(), getSessionManager());
		}
	}

	public XMLExporterService getXmlExporterService() {
		if (xmlExporter != null) return xmlExporter;
		synchronized (this) {
			if (xmlExporter != null) return xmlExporter;
			return xmlExporter = new XMLExporterService(getPersistenceService());
		}
	}

	public XMLImporterService getXmlImporterService() {
		if (xmlImporter != null) return xmlImporter;
		synchronized (this) {
			if (xmlImporter != null) return xmlImporter;
			return xmlImporter = new XMLImporterService(getPersistenceService());
		}
	}

	private BroadcastService getBroadcastService() {
		if (broadcastService != null) return broadcastService;
		return broadcastService = new BroadcastServiceImpl(getServerPushServerService());
	}

	protected PersistenceService getPersistenceService() {
		if (persistenceService != null) return persistenceService;
		synchronized (this) {
			if (persistenceService != null) return persistenceService;
			return persistenceService = new PersistenceServiceJpaImpl();
		}
	}

	public SessionManager getSessionManager() {
		if (sessionManager != null) return sessionManager;
		synchronized (this) {
			if (sessionManager != null) return sessionManager;
			return sessionManager = new SessionManager();
		}
	}

	private ServerPushServerService getServerPushServerService() {
		if (serverPushServerService != null) return serverPushServerService;
		return serverPushServerService = new ServerPushServerServiceImpl();
	}
}