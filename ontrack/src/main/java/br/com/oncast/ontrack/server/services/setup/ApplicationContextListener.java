package br.com.oncast.ontrack.server.services.setup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.zschech.gwt.comet.server.impl.AsyncServlet;
import br.com.drycode.api.web.gwt.dispatchService.server.DispatchServiceServlet;
import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchServiceException;
import br.com.oncast.ontrack.server.business.DefaultProjectExistenceAssurer;
import br.com.oncast.ontrack.server.business.DefaultUserExistenceAssurer;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.requestDispatch.ModelActionSyncRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.ProjectContextRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.ProjectCreationRequestHandler;
import br.com.oncast.ontrack.server.services.requestDispatch.ProjectListRequestHandler;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushServerService;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateProjectRepresentation;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListRequest;

public class ApplicationContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(final ServletContextEvent event) {
		setupDispatchHandlers();
		setupBusinessLogic(event);
		setupServerPush(event);
		assureDefaultUserIsPresent();
		assureDefaultProjectIsPresent();
	}

	private void setupDispatchHandlers() {
		try {
			DispatchServiceServlet.registerRequestHandler(ModelActionSyncRequest.class, new ModelActionSyncRequestHandler());
			DispatchServiceServlet.registerRequestHandler(ProjectContextRequest.class, new ProjectContextRequestHandler());
			DispatchServiceServlet.registerRequestHandler(ProjectCreationRequest.class, new ProjectCreationRequestHandler());
			DispatchServiceServlet.registerRequestHandler(ProjectListRequest.class, new ProjectListRequestHandler());
		}
		catch (final DispatchServiceException e) {
			throw new RuntimeException("The application is misconfigured.", e);
		}
	}

	@Override
	public void contextDestroyed(final ServletContextEvent event) {
		shutdownServerPush(event);
	}

	/**
	 * Sets up the business logic and - with it - all direct server services.
	 * Needed to give a chance to services like {@link ServerPushServerService} to register their listeners on the necessary application input classes (Servlets
	 * for instance).
	 * 
	 * @param event the servlet context event.
	 */
	private void setupBusinessLogic(final ServletContextEvent event) {
		ServerServiceProvider.getInstance().getBusinessLogic();
	}

	/**
	 * Assure the default user is present when the application starts.
	 */
	private void assureDefaultUserIsPresent() {
		DefaultUserExistenceAssurer.verify();
	}

	/**
	 * Assure the default project is present when the application starts.
	 * @throws UnableToCreateProjectRepresentation
	 */
	private void assureDefaultProjectIsPresent() {
		DefaultProjectExistenceAssurer.verify();
	}

	/**
	 * Initializes comet related resources associated with an ApplicationContext.
	 * Without this the comet related resources will be initialized when first used and will not be shutdown cleanly.
	 * 
	 * @param event the servlet context event.
	 */
	private void setupServerPush(final ServletContextEvent event) {
		AsyncServlet.initialize(event.getServletContext());
	}

	/**
	 * Shuts down comet related resources associated with an ApplicationContext.
	 * Without this the comet related resources will not be shutdown cleanly.
	 * 
	 * @param event the servlet context event.
	 */
	private void shutdownServerPush(final ServletContextEvent event) {
		AsyncServlet.destroy(event.getServletContext());
	}
}
