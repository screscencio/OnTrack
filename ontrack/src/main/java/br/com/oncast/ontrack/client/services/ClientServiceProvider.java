package br.com.oncast.ontrack.client.services;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchServiceDefault;
import br.com.drycode.api.web.gwt.dispatchService.client.RequestBuilderConfigurator;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.services.actionSync.ActionSyncService;
import br.com.oncast.ontrack.client.services.applicationState.ClientApplicationStateService;
import br.com.oncast.ontrack.client.services.applicationState.ClientApplicationStateServiceImpl;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationServiceImpl;
import br.com.oncast.ontrack.client.services.authorization.AuthorizationService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ContextProviderServiceImpl;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProviderImpl;
import br.com.oncast.ontrack.client.services.feedback.FeedbackService;
import br.com.oncast.ontrack.client.services.feedback.FeedbackServiceImpl;
import br.com.oncast.ontrack.client.services.identification.ClientIdentificationProvider;
import br.com.oncast.ontrack.client.services.notification.ClientNotificationService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientServiceImpl;
import br.com.oncast.ontrack.client.ui.places.AppActivityMapper;
import br.com.oncast.ontrack.client.ui.places.AppPlaceHistoryMapper;
import br.com.oncast.ontrack.shared.config.RequestConfigurations;
import br.com.oncast.ontrack.shared.exceptions.authentication.NotAuthenticatedException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;

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
	private AuthorizationService authorizationService;
	private ApplicationPlaceController placeController;
	private ClientNotificationService notificationService;

	private ClientIdentificationProvider clientIdentificationProvider;
	private ActionSyncService actionSyncService;

	private DispatchService requestDispatchService;
	private ServerPushClientService serverPushClientService;
	private EventBus eventBus;
	private FeedbackServiceImpl feedbackService;
	private ClientApplicationStateService clientApplicationStateService;

	private static ClientServiceProvider instance;

	// TODO let this method be private and make it call before other public methods.
	public static ClientServiceProvider getInstance() {
		if (instance != null) return instance;
		return instance = new ClientServiceProvider();
	}

	private ClientServiceProvider() {}

	/**
	 * Configures the necessary services for application full usage.
	 * - Initiates the {@link AuthenticationService}, which register a communication failure handler for {@link NotAuthenticatedException};
	 * - Initiates the {@link AuthenticationService}, which register a communication failure handler for {@link AuthorizationException};
	 * - Initiates the {@link ActionSyncService}, which starts a server-push connection with the server;
	 * - Initiates the {@link ApplicationPlaceController} setting the default place and panel in which the application navigation will occur.
	 * 
	 * @param panel the panel that will be used by the application "navigation" through the {@link ApplicationPlaceController}.
	 * @param defaultAppPlace the default place used by the {@link ApplicationPlaceController} "navigation".
	 */
	public void configure(final AcceptsOneWidget panel, final Place defaultAppPlace) {
		getAuthenticationService().registerAuthenticationExceptionGlobalHandler();
		getAuthorizationService().registerAuthorizationExceptionGlobalHandler();
		getActionSyncService();
		getApplicationPlaceController().configure(panel, defaultAppPlace, new AppActivityMapper(this),
				(PlaceHistoryMapper) GWT.create(AppPlaceHistoryMapper.class));
	}

	private AuthorizationService getAuthorizationService() {
		if (authorizationService != null) return authorizationService;
		return authorizationService = new AuthorizationServiceImpl(getRequestDispatchService(), getApplicationPlaceController());
	}

	public AuthenticationService getAuthenticationService() {
		if (authenticationService != null) return authenticationService;
		return authenticationService = new AuthenticationServiceImpl(getRequestDispatchService(), getApplicationPlaceController(), getServerPushClientService());
	}

	public ApplicationPlaceController getApplicationPlaceController() {
		if (placeController != null) return placeController;
		return placeController = new ApplicationPlaceController(getEventBus());
	}

	public ProjectRepresentationProvider getProjectRepresentationProvider() {
		if (projectRepresentationProvider != null) return projectRepresentationProvider;
		return projectRepresentationProvider = new ProjectRepresentationProviderImpl(getRequestDispatchService(), getServerPushClientService(),
				getAuthenticationService(), getClientNotificationService());
	}

	public ClientNotificationService getClientNotificationService() {
		if (notificationService != null) return notificationService;
		return notificationService = new ClientNotificationService();
	}

	public ActionExecutionService getActionExecutionService() {
		if (actionExecutionService != null) return actionExecutionService;
		return actionExecutionService = new ActionExecutionServiceImpl(getContextProviderService(), getClientNotificationService(),
				getProjectRepresentationProvider(), getApplicationPlaceController());
	}

	public ContextProviderService getContextProviderService() {
		if (contextProviderService != null) return contextProviderService;
		return contextProviderService = new ContextProviderServiceImpl((ProjectRepresentationProviderImpl) getProjectRepresentationProvider(),
				getRequestDispatchService(), getAuthenticationService());
	}

	public FeedbackService getFeedbackService() {
		if (feedbackService != null) return feedbackService;
		return feedbackService = new FeedbackServiceImpl(getRequestDispatchService());
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
				getProjectRepresentationProvider(), getClientNotificationService());
	}

	private ClientIdentificationProvider getClientIdentificationProvider() {
		if (clientIdentificationProvider != null) return clientIdentificationProvider;
		return clientIdentificationProvider = new ClientIdentificationProvider();
	}

	private ServerPushClientService getServerPushClientService() {
		if (serverPushClientService != null) return serverPushClientService;
		return serverPushClientService = new ServerPushClientServiceImpl(getClientIdentificationProvider(), getClientNotificationService());
	}

	public EventBus getEventBus() {
		if (eventBus != null) return eventBus;
		return eventBus = new SimpleEventBus();
	}

	public ClientApplicationStateService getClientApplicationStateService() {
		return clientApplicationStateService == null ? clientApplicationStateService = new ClientApplicationStateServiceImpl(getEventBus(), getContextProviderService()
				.getCurrentProjectContext()) : clientApplicationStateService;
	}
}
