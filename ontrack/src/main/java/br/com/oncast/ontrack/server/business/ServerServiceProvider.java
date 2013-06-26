package br.com.oncast.ontrack.server.business;

import br.com.oncast.ontrack.server.business.actionPostProcessments.ActionPostProcessmentsInitializer;
import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessingService;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authorization.AuthorizationManager;
import br.com.oncast.ontrack.server.services.authorization.AuthorizationManagerImpl;
import br.com.oncast.ontrack.server.services.email.MailFactory;
import br.com.oncast.ontrack.server.services.exportImport.xml.XMLExporterService;
import br.com.oncast.ontrack.server.services.exportImport.xml.XMLImporterService;
import br.com.oncast.ontrack.server.services.integration.BillingTrackIntegrationService;
import br.com.oncast.ontrack.server.services.integration.IntegrationService;
import br.com.oncast.ontrack.server.services.metrics.ServerMetricsService;
import br.com.oncast.ontrack.server.services.multicast.ClientManager;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.multicast.MulticastServiceImpl;
import br.com.oncast.ontrack.server.services.notification.NotificationServerService;
import br.com.oncast.ontrack.server.services.notification.NotificationServerServiceImpl;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerServiceImpl;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.server.services.storage.LocalFileSystemStorageService;
import br.com.oncast.ontrack.server.services.storage.StorageService;
import br.com.oncast.ontrack.server.services.threadSync.SyncronizationService;
import br.com.oncast.ontrack.server.services.user.UserDataManager;
import br.com.oncast.ontrack.server.services.user.UserDataManagerImpl;
import br.com.oncast.ontrack.server.services.user.UsersStatusManager;

public class ServerServiceProvider {

	private static final ServerServiceProvider INSTANCE = new ServerServiceProvider();

	private BusinessLogic businessLogic;
	private XMLExporterService xmlExporter;
	private XMLImporterService xmlImporter;

	private NotificationServerServiceImpl notificationServerService;

	private AuthorizationManager authorizationManager;
	private AuthenticationManager authenticationManager;
	private MulticastService multicastService;
	private ClientManager clientManagerService;

	private SessionManager sessionManager;
	private ServerPushServerService serverPushServerService;
	private PersistenceService persistenceService;
	private ActionPostProcessingService actionPostProcessingService;

	private MailFactory mailFactory;

	private StorageService storageService;

	private ActionPostProcessmentsInitializer postProcessmentsInitializer;

	private SyncronizationService syncronizationService;

	private UsersStatusManager usersStatusManager;

	private UserDataManager userDataManager;

	private ServerMetricsService serverMetricsService;

	private IntegrationService integrationService;

	public static ServerServiceProvider getInstance() {
		return INSTANCE;
	}

	private ServerServiceProvider() {}

	public BusinessLogic getBusinessLogic() {
		if (businessLogic != null) return businessLogic;
		synchronized (this) {
			if (businessLogic != null) return businessLogic;
			return businessLogic = new BusinessLogicImpl(getPersistenceService(), getMulticastService(), getClientManagerService(), getAuthenticationManager(), getAuthorizationManager(),
					getSessionManager(), getMailFactory(), getSyncronizationService(), getActionPostProcessmentsInitializer());
		}
	}

	public AuthorizationManager getAuthorizationManager() {
		if (authorizationManager != null) return authorizationManager;
		synchronized (this) {
			if (authorizationManager != null) return authorizationManager;
			return authorizationManager = new AuthorizationManagerImpl(getAuthenticationManager(), getPersistenceService(), getMulticastService(), getMailFactory(), getClientManagerService(),
					getIntegrationService());
		}
	}

	private IntegrationService getIntegrationService() {
		if (integrationService != null) return integrationService;
		synchronized (this) {
			if (integrationService != null) return integrationService;
			return integrationService = new BillingTrackIntegrationService();
		}
	}

	public AuthenticationManager getAuthenticationManager() {
		if (authenticationManager != null) return authenticationManager;
		synchronized (this) {
			if (authenticationManager != null) return authenticationManager;
			return authenticationManager = new AuthenticationManager(getPersistenceService(), getSessionManager(), getMailFactory());
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
			return xmlImporter = new XMLImporterService(getPersistenceService(), getBusinessLogic());
		}
	}

	private MulticastService getMulticastService() {
		if (multicastService != null) return multicastService;
		synchronized (this) {
			if (multicastService != null) return multicastService;
			return multicastService = new MulticastServiceImpl(getServerPushServerService(), getClientManagerService(), getSessionManager());
		}
	}

	public ClientManager getClientManagerService() {
		if (clientManagerService != null) return clientManagerService;
		synchronized (this) {
			if (clientManagerService != null) return clientManagerService;
			return clientManagerService = new ClientManager(getAuthenticationManager());
		}
	}

	PersistenceService getPersistenceService() {
		if (persistenceService != null) return persistenceService;
		synchronized (this) {
			if (persistenceService != null) return persistenceService;
			return persistenceService = new PersistenceServiceJpaImpl();
		}
	}

	private ActionPostProcessingService getActionPostProcessingService() {
		if (actionPostProcessingService != null) return actionPostProcessingService;
		synchronized (this) {
			if (actionPostProcessingService != null) return actionPostProcessingService;
			return actionPostProcessingService = new ActionPostProcessingService();
		}
	}

	public SessionManager getSessionManager() {
		if (sessionManager != null) return sessionManager;
		synchronized (this) {
			if (sessionManager != null) return sessionManager;
			return sessionManager = new SessionManager();
		}
	}

	public MailFactory getMailFactory() {
		if (mailFactory != null) return mailFactory;
		synchronized (this) {
			if (mailFactory != null) return mailFactory;
			return mailFactory = new MailFactory();
		}
	}

	private SyncronizationService getSyncronizationService() {
		if (syncronizationService != null) return syncronizationService;
		synchronized (this) {
			if (syncronizationService != null) return syncronizationService;
			return syncronizationService = new SyncronizationService();
		}
	}

	private ServerPushServerService getServerPushServerService() {
		if (serverPushServerService != null) return serverPushServerService;
		synchronized (this) {
			if (serverPushServerService != null) return serverPushServerService;
			return serverPushServerService = new ServerPushServerServiceImpl();
		}
	}

	public StorageService getStorageService() {
		if (storageService != null) return storageService;
		synchronized (this) {
			if (storageService != null) return storageService;
			return storageService = new LocalFileSystemStorageService(getAuthenticationManager(), getAuthorizationManager(), getPersistenceService(), getBusinessLogic());
		}
	}

	public ActionPostProcessmentsInitializer getActionPostProcessmentsInitializer() {
		if (postProcessmentsInitializer != null) return postProcessmentsInitializer;
		synchronized (this) {
			if (postProcessmentsInitializer != null) return postProcessmentsInitializer;
			return postProcessmentsInitializer = new ActionPostProcessmentsInitializer(getActionPostProcessingService(), getPersistenceService(), getMulticastService(), getNotificationServerService());
		}
	}

	public NotificationServerService getNotificationServerService() {
		if (notificationServerService != null) return notificationServerService;
		synchronized (this) {
			if (notificationServerService != null) return notificationServerService;
			return notificationServerService = new NotificationServerServiceImpl(getAuthenticationManager(), getPersistenceService(), getMulticastService());
		}
	}

	public UsersStatusManager getUsersStatusManager() {
		if (usersStatusManager != null) return usersStatusManager;
		synchronized (this) {
			if (usersStatusManager != null) return usersStatusManager;
			return usersStatusManager = new UsersStatusManager(getClientManagerService(), getMulticastService(), getAuthorizationManager());
		}
	}

	public UserDataManager getUsersDataManager() {
		if (userDataManager != null) return userDataManager;
		synchronized (this) {
			if (userDataManager != null) return userDataManager;
			return userDataManager = new UserDataManagerImpl(getPersistenceService(), getMulticastService(), getAuthorizationManager());
		}
	}

	public ServerMetricsService getServerMetricsService() {
		if (serverMetricsService != null) return serverMetricsService;
		synchronized (this) {
			if (serverMetricsService != null) return serverMetricsService;
			return serverMetricsService = new ServerMetricsService(getPersistenceService(), getClientManagerService(), getBusinessLogic());
		}
	}
}