package br.com.oncast.ontrack.client.services;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchServiceDefault;
import br.com.drycode.api.web.gwt.dispatchService.client.RequestBuilderConfigurator;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.services.actionSync.ActionSyncService;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.annotations.AnnotationService;
import br.com.oncast.ontrack.client.services.annotations.AnnotationServiceImpl;
import br.com.oncast.ontrack.client.services.applicationState.ClientApplicationStateService;
import br.com.oncast.ontrack.client.services.applicationState.ClientApplicationStateServiceImpl;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationServiceImpl;
import br.com.oncast.ontrack.client.services.authorization.AuthorizationService;
import br.com.oncast.ontrack.client.services.checklist.ChecklistService;
import br.com.oncast.ontrack.client.services.checklist.ChecklistServiceImpl;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ContextProviderServiceImpl;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProviderImpl;
import br.com.oncast.ontrack.client.services.feedback.FeedbackService;
import br.com.oncast.ontrack.client.services.feedback.FeedbackServiceImpl;
import br.com.oncast.ontrack.client.services.metric.ClientMetricService;
import br.com.oncast.ontrack.client.services.metric.ClientMetricServiceNewRelicImpl;
import br.com.oncast.ontrack.client.services.notification.NotificationService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientServiceImpl;
import br.com.oncast.ontrack.client.services.storage.ClientStorageService;
import br.com.oncast.ontrack.client.services.storage.Html5StorageClientStorageService;
import br.com.oncast.ontrack.client.services.user.UserDataService;
import br.com.oncast.ontrack.client.services.user.UserDataServiceImpl;
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
	private ClientAlertingService alertService;
	private NotificationService notificationService;

	private ActionSyncService actionSyncService;

	private DispatchService requestDispatchService;
	private ServerPushClientService serverPushClientService;
	private EventBus eventBus;
	private FeedbackServiceImpl feedbackService;
	private ClientApplicationStateService clientApplicationStateService;

	private AnnotationService annotationService;
	private UserDataService userDataService;
	private ChecklistService checklistService;
	private ClientStorageService clientStorageService;
	private ClientMetricService clientMetricService;

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
				getAuthenticationService(), getClientAlertingService());
	}

	public ClientAlertingService getClientAlertingService() {
		if (alertService != null) return alertService;
		return alertService = new ClientAlertingService();
	}

	public ActionExecutionService getActionExecutionService() {
		if (actionExecutionService != null) return actionExecutionService;
		return actionExecutionService = new ActionExecutionServiceImpl(getContextProviderService(), getClientAlertingService(),
				getProjectRepresentationProvider(), getApplicationPlaceController(), getAuthenticationService());
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
				requestBuilder
						.setHeader(RequestConfigurations.CLIENT_IDENTIFICATION_PARAMETER_NAME, getServerPushClientService().getConnectionID());
			}
		});
	}

	private ActionSyncService getActionSyncService() {
		if (actionSyncService != null) return actionSyncService;
		return actionSyncService = new ActionSyncService(getRequestDispatchService(), getServerPushClientService(), getActionExecutionService(),
				getProjectRepresentationProvider(), getClientAlertingService());
	}

	public ServerPushClientService getServerPushClientService() {
		if (serverPushClientService != null) return serverPushClientService;
		return serverPushClientService = new ServerPushClientServiceImpl(getClientAlertingService());
	}

	public EventBus getEventBus() {
		if (eventBus != null) return eventBus;
		return eventBus = new SimpleEventBus();
	}

	public ClientApplicationStateService getClientApplicationStateService() {
		return clientApplicationStateService == null ? clientApplicationStateService = new ClientApplicationStateServiceImpl(getEventBus(),
				getContextProviderService(), getClientStorageService()) : clientApplicationStateService;
	}

	public ClientStorageService getClientStorageService() {
		if (clientStorageService == null) clientStorageService = new Html5StorageClientStorageService(getAuthenticationService(),
				getProjectRepresentationProvider());
		return clientStorageService;
	}

	public AnnotationService getAnnotationService() {
		if (annotationService != null) return annotationService;
		return annotationService = new AnnotationServiceImpl(getActionExecutionService(), getContextProviderService(),
				getApplicationPlaceController(), getEventBus());
	}

	public UserDataService getUserDataService() {
		if (userDataService == null) userDataService = new UserDataServiceImpl();
		return userDataService;
	}

	public ChecklistService getChecklistService() {
		if (checklistService == null) checklistService = new ChecklistServiceImpl(getActionExecutionService());
		return checklistService;
	}

	public ClientMetricService getClientMetricService() {
		if (clientMetricService == null) clientMetricService = new ClientMetricServiceNewRelicImpl();
		return clientMetricService;
	}

	public NotificationService getNotificationService() {
		if (notificationService == null) notificationService = new NotificationService(getRequestDispatchService(), getServerPushClientService(),
				getAuthenticationService(), getClientAlertingService());
		return notificationService;
	}
}
