package br.com.oncast.ontrack.client.services;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchServiceDefault;
import br.com.drycode.api.web.gwt.dispatchService.client.RequestBuilderConfigurator;
import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionServiceImpl;
import br.com.oncast.ontrack.client.services.actionSync.ActionSyncService;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
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
import br.com.oncast.ontrack.client.services.details.DetailService;
import br.com.oncast.ontrack.client.services.details.DetailServiceImpl;
import br.com.oncast.ontrack.client.services.estimator.ReleaseEstimatorProvider;
import br.com.oncast.ontrack.client.services.feedback.FeedbackService;
import br.com.oncast.ontrack.client.services.feedback.FeedbackServiceImpl;
import br.com.oncast.ontrack.client.services.instruction.UserGuidService;
import br.com.oncast.ontrack.client.services.instruction.UserGuideServiceImpl;
import br.com.oncast.ontrack.client.services.metrics.ClientMetricsService;
import br.com.oncast.ontrack.client.services.metrics.ClientMetricsServiceImpl;
import br.com.oncast.ontrack.client.services.notification.NotificationService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientServiceImpl;
import br.com.oncast.ontrack.client.services.storage.ClientStorageService;
import br.com.oncast.ontrack.client.services.storage.Html5StorageClientStorageService;
import br.com.oncast.ontrack.client.services.timesheet.TimesheetService;
import br.com.oncast.ontrack.client.services.timesheet.TimesheetServiceImpl;
import br.com.oncast.ontrack.client.services.user.ColorPackPicker;
import br.com.oncast.ontrack.client.services.user.ColorPicker;
import br.com.oncast.ontrack.client.services.user.ColorProviderService;
import br.com.oncast.ontrack.client.services.user.ColorProviderServiceImpl;
import br.com.oncast.ontrack.client.services.user.UserAssociationService;
import br.com.oncast.ontrack.client.services.user.UserAssociationServiceImpl;
import br.com.oncast.ontrack.client.services.user.UserDataService;
import br.com.oncast.ontrack.client.services.user.UserDataServiceImpl;
import br.com.oncast.ontrack.client.services.user.UsersStatusService;
import br.com.oncast.ontrack.client.services.user.UsersStatusServiceImpl;
import br.com.oncast.ontrack.client.ui.places.AppActivityMapper;
import br.com.oncast.ontrack.client.ui.places.AppPlaceHistoryMapper;
import br.com.oncast.ontrack.shared.config.RequestConfigurations;
import br.com.oncast.ontrack.shared.exceptions.authentication.NotAuthenticatedException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The {@link ClientServices} is programmed in such a way that only business services are publicly available.
 * Both infrastructure and "glue" services should be private.
 * 
 * "DEPENDENCY INJECTION vs LOCALIZATION" POLICY
 * - Services should be injected when in other services, in order to favor testing;
 * - Services should be located through the singleton usage when needed at the UI, if only used carefully, so that DI cascading is evicted.
 * DI cascading produces a lot of "dirty code" with "delegators" and also has implies in the need of lots of custom factories in UIBinder objects.
 */
public class ClientServices {

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

	private DetailService annotationService;
	private UserDataService userDataService;
	private ChecklistService checklistService;
	private ClientStorageService clientStorageService;
	private UsersStatusService usersStatusService;
	private ColorProviderService colorProviderService;

	private ClientErrorMessages clientErrorMessages;
	private UserGuidService userGuidService;
	private UserAssociationService userAssociationService;
	private ClientMetricsService clientMetricsService;

	private TimesheetService timesheetService;
	private ReleaseEstimatorProvider releaseEstimatorProvider;

	private static ClientServices instance;

	public static ClientServices get() {
		if (instance != null) return instance;
		return instance = new ClientServices();
	}

	private ClientServices() {}

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
		authentication().registerAuthenticationExceptionGlobalHandler();
		authorization().registerAuthorizationExceptionGlobalHandler();
		actionSync();
		placeController().configure(panel, defaultAppPlace, new AppActivityMapper(this),
				(PlaceHistoryMapper) GWT.create(AppPlaceHistoryMapper.class), storage());
		colorProvider();
	}

	private AuthorizationService authorization() {
		if (authorizationService != null) return authorizationService;
		return authorizationService = new AuthorizationServiceImpl(request(), placeController(), contextProvider());
	}

	public AuthenticationService authentication() {
		if (authenticationService != null) return authenticationService;
		return authenticationService = new AuthenticationServiceImpl(request(), placeController(),
				serverPush());
	}

	public ApplicationPlaceController placeController() {
		if (placeController != null) return placeController;
		return placeController = new ApplicationPlaceController(eventBus());
	}

	public ProjectRepresentationProvider projectRepresentationProvider() {
		if (projectRepresentationProvider != null) return projectRepresentationProvider;
		return projectRepresentationProvider = new ProjectRepresentationProviderImpl(request(), serverPush(),
				authentication(), alerting(), errorMessages());
	}

	public ClientAlertingService alerting() {
		if (alertService != null) return alertService;
		return alertService = new ClientAlertingService();
	}

	public ActionExecutionService actionExecution() {
		if (actionExecutionService != null) return actionExecutionService;
		return actionExecutionService = new ActionExecutionServiceImpl(contextProvider(), alerting(),
				projectRepresentationProvider(), placeController(), authentication());
	}

	public ContextProviderService contextProvider() {
		if (contextProviderService != null) return contextProviderService;
		return contextProviderService = new ContextProviderServiceImpl((ProjectRepresentationProviderImpl) projectRepresentationProvider(),
				request(), authentication());
	}

	public FeedbackService feedback() {
		if (feedbackService != null) return feedbackService;
		return feedbackService = new FeedbackServiceImpl(request());
	}

	private DispatchService request() {
		if (requestDispatchService != null) return requestDispatchService;
		return requestDispatchService = new DispatchServiceDefault(new RequestBuilderConfigurator() {
			@Override
			public void configureRequestBuilder(final RequestBuilder requestBuilder) {
				requestBuilder
						.setHeader(RequestConfigurations.CLIENT_IDENTIFICATION_PARAMETER_NAME, serverPush().getConnectionID());
			}
		});
	}

	private ActionSyncService actionSync() {
		if (actionSyncService != null) return actionSyncService;
		return actionSyncService = new ActionSyncService(request(), serverPush(), actionExecution(),
				projectRepresentationProvider(), alerting(), errorMessages());
	}

	public ServerPushClientService serverPush() {
		if (serverPushClientService != null) return serverPushClientService;
		return serverPushClientService = new ServerPushClientServiceImpl(alerting(), errorMessages());
	}

	public EventBus eventBus() {
		if (eventBus != null) return eventBus;
		return eventBus = new SimpleEventBus();
	}

	public ClientApplicationStateService applicationState() {
		return clientApplicationStateService == null ? clientApplicationStateService = new ClientApplicationStateServiceImpl(eventBus(),
				contextProvider(), storage(), alerting(), errorMessages()) : clientApplicationStateService;
	}

	public ClientStorageService storage() {
		if (clientStorageService == null) clientStorageService = new Html5StorageClientStorageService(authentication(),
				projectRepresentationProvider());
		return clientStorageService;
	}

	public DetailService details() {
		if (annotationService != null) return annotationService;
		return annotationService = new DetailServiceImpl(actionExecution(), contextProvider(),
				placeController(), eventBus());
	}

	public UserDataService userData() {
		if (userDataService == null) userDataService = new UserDataServiceImpl(request(), contextProvider(),
				serverPush());
		return userDataService;
	}

	public ChecklistService checklists() {
		if (checklistService == null) checklistService = new ChecklistServiceImpl(actionExecution());
		return checklistService;
	}

	public NotificationService notifications() {
		if (notificationService == null) notificationService = new NotificationService(request(), serverPush(),
				projectRepresentationProvider(), alerting());
		return notificationService;
	}

	public UsersStatusService usersStatus() {
		if (usersStatusService == null) usersStatusService = new UsersStatusServiceImpl(request(), contextProvider(),
				serverPush(), eventBus());
		return usersStatusService;
	}

	public ColorProviderService colorProvider() {
		if (colorProviderService == null) colorProviderService = new ColorProviderServiceImpl(request(),
				contextProvider(), serverPush(), eventBus(), usersStatus(), new ColorPicker(), new ColorPackPicker());
		return colorProviderService;
	}

	public ClientErrorMessages errorMessages() {
		return clientErrorMessages == null ? clientErrorMessages = GWT.create(ClientErrorMessages.class) : clientErrorMessages;
	}

	public UserGuidService userGuide() {
		return userGuidService == null ? userGuidService = new UserGuideServiceImpl() : userGuidService;
	}

	public UserAssociationService userAssociation() {
		return userAssociationService == null ? userAssociationService = new UserAssociationServiceImpl(actionExecution(),
				contextProvider()) : userAssociationService;
	}

	public ReleaseEstimatorProvider releaseEstimator() {
		return releaseEstimatorProvider == null ? releaseEstimatorProvider = new ReleaseEstimatorProvider(contextProvider())
				: releaseEstimatorProvider;
	}

	public static ProjectContext getCurrentProjectContext() {
		return get().contextProvider().getCurrent();
	}

	public static UUID getCurrentUser() {
		return get().authentication().getCurrentUserId();
	}

	public ClientMetricsService getClientMetricsService() {
		return clientMetricsService == null ? clientMetricsService = new ClientMetricsServiceImpl(request()) : clientMetricsService;
	}

	public TimesheetService getTimesheetService() {
		if (timesheetService == null) timesheetService = new TimesheetServiceImpl(placeController(), contextProvider());
		return timesheetService;
	}
}
