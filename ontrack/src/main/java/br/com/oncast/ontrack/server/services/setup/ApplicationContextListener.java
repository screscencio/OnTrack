package br.com.oncast.ontrack.server.services.setup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import br.com.drycode.api.web.gwt.dispatchService.server.DispatchServiceServlet;
import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchServiceException;
import br.com.oncast.ontrack.client.services.feedback.SendFeedbackRequest;
import br.com.oncast.ontrack.server.business.DefaultUserExistenceAssurer;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationVerificationAspectFilter;
import br.com.oncast.ontrack.server.services.requestDispatch.AuthenticationRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.ChangePasswordRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.CurrentUserInformationRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.DeAuthenticationRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.ModelActionSyncEventRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.ModelActionSyncRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.MultipleProjectContextRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.NotificationListRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.NotificationReadStateRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.PasswordResetRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.ProjectAuthorizationRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.ProjectContextRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.ProjectCreationQuotaRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.ProjectCreationRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.ProjectListRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.RemoveProjectAuthorizationRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.SendFeedbackRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.UserDataRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.UserDataUpdateRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.UserScopeSelectionMulticastRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.UsersStatusRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.admin.OnTrackServerStatisticsRequestHandler;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ChangePasswordRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.CurrentUserInformationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.DeAuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncEventRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.MultipleProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.NotificationListRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.NotificationReadStateRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.PasswordResetRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectAuthorizationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationQuotaRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.RemoveProjectAuthorizationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.UserDataRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.UserDataUpdateRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.UserScopeSelectionMulticastRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.UsersStatusRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.admin.OnTrackServerStatisticsRequest;

public class ApplicationContextListener implements ServletContextListener {

	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();

	@Override
	public void contextInitialized(final ServletContextEvent event) {
		setupDispatchHandlers();
		setupAuthenticationAspectIntoDispatchService();
		setupBusinessLogic(event);
		setupUsersStatusManager();
		assureDefaultUserIsPresent();
	}

	private void setupUsersStatusManager() {
		SERVICE_PROVIDER.getUsersStatusManager();
	}

	private void setupAuthenticationAspectIntoDispatchService() {
		DispatchServiceServlet.registerRequestFilter(new AuthenticationVerificationAspectFilter(SERVICE_PROVIDER.getAuthenticationManager()));
	}

	// TODO +++ Maybe change this registration to Annotation based one;
	private void setupDispatchHandlers() {
		try {
			DispatchServiceServlet.registerRequestHandler(ModelActionSyncRequest.class, new ModelActionSyncRequestHandler());
			DispatchServiceServlet.registerRequestHandler(ProjectContextRequest.class, new ProjectContextRequestHandler());
			DispatchServiceServlet.registerRequestHandler(ProjectCreationRequest.class, new ProjectCreationRequestHandler());
			DispatchServiceServlet.registerRequestHandler(ProjectAuthorizationRequest.class, new ProjectAuthorizationRequestHandler());
			DispatchServiceServlet.registerRequestHandler(ProjectListRequest.class, new ProjectListRequestHandler());
			DispatchServiceServlet.registerRequestHandler(AuthenticationRequest.class, new AuthenticationRequestHandler());
			DispatchServiceServlet.registerRequestHandler(PasswordResetRequest.class, new PasswordResetRequestHandler());
			DispatchServiceServlet.registerRequestHandler(DeAuthenticationRequest.class, new DeAuthenticationRequestHandler());
			DispatchServiceServlet.registerRequestHandler(ChangePasswordRequest.class, new ChangePasswordRequestHandler());
			DispatchServiceServlet.registerRequestHandler(CurrentUserInformationRequest.class, new CurrentUserInformationRequestHandler());
			DispatchServiceServlet.registerRequestHandler(ProjectCreationQuotaRequest.class, new ProjectCreationQuotaRequestHandler());
			DispatchServiceServlet.registerRequestHandler(SendFeedbackRequest.class, new SendFeedbackRequestHandler());
			DispatchServiceServlet.registerRequestHandler(NotificationListRequest.class, new NotificationListRequestHandler());
			DispatchServiceServlet.registerRequestHandler(UsersStatusRequest.class, new UsersStatusRequestHandler());
			DispatchServiceServlet.registerRequestHandler(UserDataUpdateRequest.class, new UserDataUpdateRequestHandler());
			DispatchServiceServlet.registerRequestHandler(UserDataRequest.class, new UserDataRequestHandler());
			DispatchServiceServlet.registerRequestHandler(UserScopeSelectionMulticastRequest.class, new UserScopeSelectionMulticastRequestHandler());
			DispatchServiceServlet.registerRequestHandler(NotificationReadStateRequest.class, new NotificationReadStateRequestHandler());
			DispatchServiceServlet.registerRequestHandler(MultipleProjectContextRequest.class, new MultipleProjectContextRequestHandler());
			DispatchServiceServlet.registerRequestHandler(RemoveProjectAuthorizationRequest.class, new RemoveProjectAuthorizationRequestHandler());
			DispatchServiceServlet.registerRequestHandler(OnTrackServerMetricsRequest.class, new OnTrackServerMetricsRequestHandler());
			DispatchServiceServlet.registerRequestHandler(OnTrackServerStatisticsRequest.class, new OnTrackServerStatisticsRequestHandler());
			DispatchServiceServlet.registerRequestHandler(ModelActionSyncEventRequest.class, new ModelActionSyncEventRequestHandler());
		}
		catch (final DispatchServiceException e) {
			throw new RuntimeException("The application is misconfigured.", e);
		}
	}

	@Override
	public void contextDestroyed(final ServletContextEvent event) {}

	/**
	 * Sets up the business logic and - with it - all direct server services.
	 * Needed to give a chance to services like {@link ServerPushServerService} to register their listeners on the necessary application input classes (Servlets
	 * for instance).
	 * 
	 * @param event the servlet context event.
	 */
	private void setupBusinessLogic(final ServletContextEvent event) {
		SERVICE_PROVIDER.getBusinessLogic();
		setupActionPostProcessors();
	}

	/**
	 * Registers all default action post processors.
	 */
	private void setupActionPostProcessors() {
		SERVICE_PROVIDER.getActionPostProcessmentsInitializer().initialize();
	}

	/**
	 * Assure the default user is present when the application starts.
	 */
	private void assureDefaultUserIsPresent() {
		DefaultUserExistenceAssurer.verify();
	}
}
