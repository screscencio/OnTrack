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
import br.com.oncast.ontrack.server.services.requestDispatch.ModelActionSyncRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.NotificationListRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.ProjectAuthorizationRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.ProjectContextRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.ProjectCreationQuotaRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.ProjectCreationRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.ProjectListRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.SendFeedbackRequestHandler;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ChangePasswordRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.CurrentUserInformationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.DeAuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.NotificationListRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectAuthorizationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationQuotaRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListRequest;

public class ApplicationContextListener implements ServletContextListener {

	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();

	@Override
	public void contextInitialized(final ServletContextEvent event) {
		setupDispatchHandlers();
		setupAuthenticationAspectIntoDispatchService();
		setupBusinessLogic(event);
		assureDefaultUserIsPresent();
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
			DispatchServiceServlet.registerRequestHandler(DeAuthenticationRequest.class, new DeAuthenticationRequestHandler());
			DispatchServiceServlet.registerRequestHandler(ChangePasswordRequest.class, new ChangePasswordRequestHandler());
			DispatchServiceServlet.registerRequestHandler(CurrentUserInformationRequest.class, new CurrentUserInformationRequestHandler());
			DispatchServiceServlet.registerRequestHandler(ProjectCreationQuotaRequest.class, new ProjectCreationQuotaRequestHandler());
			DispatchServiceServlet.registerRequestHandler(SendFeedbackRequest.class, new SendFeedbackRequestHandler());
			DispatchServiceServlet.registerRequestHandler(NotificationListRequest.class, new NotificationListRequestHandler());
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
