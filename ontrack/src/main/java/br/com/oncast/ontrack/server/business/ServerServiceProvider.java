package br.com.oncast.ontrack.server.business;

import br.com.oncast.ontrack.server.business.actionPostProcessments.ActionPostProcessmentsInitializer;
import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessingService;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authorization.AuthorizationManager;
import br.com.oncast.ontrack.server.services.authorization.AuthorizationManagerImpl;
import br.com.oncast.ontrack.server.services.email.FeedbackMailFactory;
import br.com.oncast.ontrack.server.services.email.ProjectAuthorizationMailFactory;
import br.com.oncast.ontrack.server.services.exportImport.xml.XMLExporterService;
import br.com.oncast.ontrack.server.services.exportImport.xml.XMLImporterService;
import br.com.oncast.ontrack.server.services.notification.ClientManager;
import br.com.oncast.ontrack.server.services.notification.NotificationService;
import br.com.oncast.ontrack.server.services.notification.NotificationServiceImpl;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerServiceImpl;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.server.services.storage.LocalFileSystemStorageService;
import br.com.oncast.ontrack.server.services.storage.StorageService;

public class ServerServiceProvider {

	private static final ServerServiceProvider INSTANCE = new ServerServiceProvider();

	private BusinessLogic businessLogic;
	private XMLExporterService xmlExporter;
	private XMLImporterService xmlImporter;

	private AuthorizationManager authorizationManager;
	private AuthenticationManager authenticationManager;
	private NotificationService notificationService;
	private ClientManager clientManagerService;

	private SessionManager sessionManager;
	private ServerPushServerService serverPushServerService;
	private PersistenceService persistenceService;
	private ActionPostProcessingService actionPostProcessingService;

	private ProjectAuthorizationMailFactory projectAuthorizationMailFactory;
	private FeedbackMailFactory userQuotaRequestMailFactory;

	private StorageService storageService;

	private ActionPostProcessmentsInitializer postProcessmentsInitializer;

	public static ServerServiceProvider getInstance() {
		return INSTANCE;
	}

	private ServerServiceProvider() {}

	public BusinessLogic getBusinessLogic() {
		if (businessLogic != null) return businessLogic;
		synchronized (this) {
			if (businessLogic != null) return businessLogic;
			return businessLogic = new BusinessLogicImpl(getActionPostProcessingService(), getPersistenceService(), getNotificationService(),
					getClientManagerService(), getAuthenticationManager(), getAuthorizationManager(), getSessionManager(), getFeedbackMailFactory());
		}
	}

	public AuthorizationManager getAuthorizationManager() {
		if (authorizationManager != null) return authorizationManager;
		synchronized (this) {
			if (authorizationManager != null) return authorizationManager;
			return authorizationManager = new AuthorizationManagerImpl(getAuthenticationManager(), getPersistenceService(), getNotificationService(),
					getProjectAuthorizationMailFactory());
		}
	}

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
			return xmlImporter = new XMLImporterService(getPersistenceService(), getBusinessLogic());
		}
	}

	private NotificationService getNotificationService() {
		if (notificationService != null) return notificationService;
		synchronized (this) {
			if (notificationService != null) return notificationService;
			return notificationService = new NotificationServiceImpl(getServerPushServerService(), getClientManagerService(), getSessionManager());
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

	public ProjectAuthorizationMailFactory getProjectAuthorizationMailFactory() {
		if (projectAuthorizationMailFactory != null) return projectAuthorizationMailFactory;
		synchronized (this) {
			if (projectAuthorizationMailFactory != null) return projectAuthorizationMailFactory;
			return projectAuthorizationMailFactory = new ProjectAuthorizationMailFactory();
		}
	}

	private FeedbackMailFactory getFeedbackMailFactory() {
		if (userQuotaRequestMailFactory != null) return userQuotaRequestMailFactory;
		synchronized (this) {
			if (userQuotaRequestMailFactory != null) return userQuotaRequestMailFactory;
			return userQuotaRequestMailFactory = new FeedbackMailFactory();
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
			return storageService = new LocalFileSystemStorageService(getAuthenticationManager(), getAuthorizationManager(), getPersistenceService(),
					getBusinessLogic());
		}
	}

	public ActionPostProcessmentsInitializer getActionPostProcessmentsInitializer() {
		if (postProcessmentsInitializer != null) return postProcessmentsInitializer;
		synchronized (this) {
			if (postProcessmentsInitializer != null) return postProcessmentsInitializer;
			return postProcessmentsInitializer = new ActionPostProcessmentsInitializer(getActionPostProcessingService(), getPersistenceService(),
					getNotificationService());
		}
	}
}