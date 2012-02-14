package br.com.oncast.ontrack.client.services;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchServiceDefault;
import br.com.drycode.api.web.gwt.dispatchService.client.RequestBuilderConfigurator;
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
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientServiceImpl;
import br.com.oncast.ontrack.client.ui.places.AppActivityMapper;
import br.com.oncast.ontrack.client.ui.places.AppPlaceHistoryMapper;
import br.com.oncast.ontrack.shared.config.RequestConfigurations;
import br.com.oncast.ontrack.shared.exceptions.authentication.NotAuthenticatedException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The {@link ClientServiceProvider} is programmed in such a way that only business services are publicly available.
 * Both infrastructure and "glue" services should be private.
 * 
 * "DEPENDENCY INJECTION vs LOCALIZATION" POLICY
 * - Services should be injected when in other services, in order to favor testing;
 * - Services should be located through the singleton usage when needed at the UI, if only used carefully, so that DI cascading is evicted.
 * DI cascading produces a lot of "dirty code" with "delegators" and also has implies in the need of lots of custom factories in UIBinder objects.
 */
public class ClientServiceProvider {

	private ActionExecutionService actionExecutionService;
	private ContextProviderService contextProviderService;
	private ProjectRepresentationProvider projectRepresentationProvider;

	private AuthenticationService authenticationService;
	private ApplicationPlaceController placeController;

	private ClientIdentificationProvider clientIdentificationProvider;
	private ActionSyncService actionSyncService;

	private DispatchService requestDispatchService;
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
	 * - Initiates the {@link AuthenticationService}, which register a communication failure handler for {@link NotAuthenticatedException};
	 * - Initiates the {@link ActionSyncService}, which starts a server-push connection with the server;
	 * - Initiates the {@link ErrorTreatmentService}, which starts an global error handler;
	 * - Initiates the {@link ApplicationPlaceController} setting the default place and panel in which the application navigation will occur.
	 * 
	 * @param panel the panel that will be used by the application "navigation" through the {@link ApplicationPlaceController}.
	 * @param defaultAppPlace the default place used by the {@link ApplicationPlaceController} "navigation".
	 */
	public void configure(final AcceptsOneWidget panel, final Place defaultAppPlace) {
		getAuthenticationService();
		getActionSyncService();
		getErrorTreatmentService();
		getApplicationPlaceController().configure(panel, defaultAppPlace, new AppActivityMapper(this),
				(PlaceHistoryMapper) GWT.create(AppPlaceHistoryMapper.class));
	}

	public AuthenticationService getAuthenticationService() {
		if (authenticationService != null) return authenticationService;
		return authenticationService = new AuthenticationServiceImpl(getRequestDispatchService(), getApplicationPlaceController());
	}

	public ApplicationPlaceController getApplicationPlaceController() {
		if (placeController != null) return placeController;
		return placeController = new ApplicationPlaceController(getEventBus());
	}

	public ProjectRepresentationProvider getProjectRepresentationProvider() {
		if (projectRepresentationProvider != null) return projectRepresentationProvider;
		return projectRepresentationProvider = new ProjectRepresentationProviderImpl(getRequestDispatchService(), getServerPushClientService(),
				getAuthenticationService());
	}

	public ActionExecutionService getActionExecutionService() {
		if (actionExecutionService != null) return actionExecutionService;
		return actionExecutionService = new ActionExecutionServiceImpl(getContextProviderService(), getErrorTreatmentService(),
				getProjectRepresentationProvider(), getApplicationPlaceController());
	}

	public ContextProviderService getContextProviderService() {
		if (contextProviderService != null) return contextProviderService;
		return contextProviderService = new ContextProviderServiceImpl((ProjectRepresentationProviderImpl) getProjectRepresentationProvider(),
				getRequestDispatchService(), getAuthenticationService());
	}

	public ErrorTreatmentService getErrorTreatmentService() {
		if (errorTreatmentService != null) return errorTreatmentService;
		return errorTreatmentService = new ErrorTreatmentServiceImpl();
	}

	private DispatchService getRequestDispatchService() {
		if (requestDispatchService != null) return requestDispatchService;
		return requestDispatchService = new DispatchServiceDefault(new RequestBuilderConfigurator() {

			@Override
			public void configureRequestBuilder(final RequestBuilder requestBuilder) {
				requestBuilder.setHeader(RequestConfigurations.CLIENT_IDENTIFICATION_HEADER, getClientIdentificationProvider().getClientId().toString());
			}
		});
	}

	private ActionSyncService getActionSyncService() {
		if (actionSyncService != null) return actionSyncService;
		return actionSyncService = new ActionSyncService(getRequestDispatchService(), getServerPushClientService(), getActionExecutionService(),
				getProjectRepresentationProvider(), getErrorTreatmentService());
	}

	private ClientIdentificationProvider getClientIdentificationProvider() {
		if (clientIdentificationProvider != null) return clientIdentificationProvider;
		return clientIdentificationProvider = new ClientIdentificationProvider();
	}

	private ServerPushClientService getServerPushClientService() {
		if (serverPushClientService != null) return serverPushClientService;
		return serverPushClientService = new ServerPushClientServiceImpl(getClientIdentificationProvider(), getErrorTreatmentService());
	}

	private EventBus getEventBus() {
		if (eventBus != null) return eventBus;
		return eventBus = new SimpleEventBus();
	}
}
