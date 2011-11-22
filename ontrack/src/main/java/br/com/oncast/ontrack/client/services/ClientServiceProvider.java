package br.com.oncast.ontrack.client.services;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.services.actionSync.ActionSyncService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationServiceImpl;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ContextProviderServiceImpl;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProviderImpl;
import br.com.oncast.ontrack.client.services.errorHandling.ErrorTreatmentService;
import br.com.oncast.ontrack.client.services.errorHandling.ErrorTreatmentServiceImpl;
import br.com.oncast.ontrack.client.services.identification.ClientIdentificationProvider;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.services.requestDispatch.RequestDispatchService;
import br.com.oncast.ontrack.client.services.requestDispatch.RequestDispatchServiceImpl;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientServiceImpl;
import br.com.oncast.ontrack.client.ui.places.AppActivityMapper;
import br.com.oncast.ontrack.client.ui.places.AppPlaceHistoryMapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

// TODO Create interfaces for each service and return them instead of the direct reference of its implementations (so that the rest of the application only
// reference the interfaces, making the code more testable).
// FIXME Use location at the UI and hide infra services, such as ActionSyncService and etc.
public class ClientServiceProvider {

	private ActionExecutionService actionExecutionService;
	private ContextProviderService contextProviderService;
	private ProjectRepresentationProvider projectRepresentationProvider;

	private AuthenticationService authenticationService;
	private ApplicationPlaceController placeController;

	private ClientIdentificationProvider clientIdentificationProvider;
	private ActionSyncService actionSyncService;

	private RequestDispatchService requestDispatchService;
	private ServerPushClientService serverPushClientService;
	private ErrorTreatmentService errorTreatmentService;
	private EventBus eventBus;

	private static ClientServiceProvider instance;

	public static ClientServiceProvider getInstance() {
		if (instance != null) return instance;
		return instance = new ClientServiceProvider();
	}

	private ClientServiceProvider() {}

	/**
	 * Configures the necessary services for application full usage.
	 * - Initiates the {@link ActionSyncService}, which starts a server-push connection with the server;
	 * - Initiates the {@link ApplicationPlaceController} setting the default place and panel in which the application navigation will occur.
	 * 
	 * @param panel the panel that will be used by the application "navigation" through the {@link ApplicationPlaceController}.
	 * @param defaultAppPlace the default place used by the {@link ApplicationPlaceController} "navigation".
	 */
	public void configure(final AcceptsOneWidget panel, final Place defaultAppPlace) {
		getActionSyncService();

		getApplicationPlaceController().configure(panel, defaultAppPlace, new AppActivityMapper(this),
				(PlaceHistoryMapper) GWT.create(AppPlaceHistoryMapper.class));
	}

	public AuthenticationService getAuthenticationService() {
		if (authenticationService != null) return authenticationService;
		return authenticationService = new AuthenticationServiceImpl(requestDispatchService);
	}

	public ApplicationPlaceController getApplicationPlaceController() {
		if (placeController != null) return placeController;
		return placeController = new ApplicationPlaceController(getEventBus());
	}

	public ProjectRepresentationProvider getProjectRepresentationProvider() {
		if (projectRepresentationProvider != null) return projectRepresentationProvider;
		return projectRepresentationProvider = new ProjectRepresentationProviderImpl(getRequestDispatchService(), getServerPushClientService());
	}

	public ActionExecutionService getActionExecutionService() {
		if (actionExecutionService != null) return actionExecutionService;
		return actionExecutionService = new ActionExecutionServiceImpl(getContextProviderService(), getErrorTreatmentService(),
				getProjectRepresentationProvider());
	}

	public ContextProviderService getContextProviderService() {
		if (contextProviderService != null) return contextProviderService;
		return contextProviderService = new ContextProviderServiceImpl((ProjectRepresentationProviderImpl) getProjectRepresentationProvider(),
				getRequestDispatchService());
	}

	private RequestDispatchService getRequestDispatchService() {
		if (requestDispatchService != null) return requestDispatchService;
		return requestDispatchService = new RequestDispatchServiceImpl();
	}

	private ActionSyncService getActionSyncService() {
		if (actionSyncService != null) return actionSyncService;
		return actionSyncService = new ActionSyncService(getRequestDispatchService(), getServerPushClientService(), getActionExecutionService(),
				getClientIdentificationProvider(), getProjectRepresentationProvider(), getErrorTreatmentService());
	}

	private ErrorTreatmentService getErrorTreatmentService() {
		if (errorTreatmentService != null) return errorTreatmentService;
		return errorTreatmentService = new ErrorTreatmentServiceImpl();
	}

	private ClientIdentificationProvider getClientIdentificationProvider() {
		if (clientIdentificationProvider != null) return clientIdentificationProvider;
		return clientIdentificationProvider = new ClientIdentificationProvider();
	}

	private ServerPushClientService getServerPushClientService() {
		if (serverPushClientService != null) return serverPushClientService;
		return serverPushClientService = new ServerPushClientServiceImpl(getErrorTreatmentService());
	}

	private EventBus getEventBus() {
		if (eventBus != null) return eventBus;
		return eventBus = new SimpleEventBus();
	}
}
