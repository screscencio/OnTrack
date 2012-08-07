package br.com.oncast.ontrack.server.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.AnnotationCreatePostProcessor;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.server.services.authorization.AuthorizationManager;
import br.com.oncast.ontrack.server.services.email.FeedbackMailFactory;
import br.com.oncast.ontrack.server.services.notification.ClientManager;
import br.com.oncast.ontrack.server.services.notification.NotificationService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.exceptions.business.InvalidIncomingAction;
import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateProjectRepresentation;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveProjectListException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.FileUploadAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;

class BusinessLogicImpl implements BusinessLogic {

	private static final Logger LOGGER = Logger.getLogger(BusinessLogicImpl.class);

	private final PersistenceService persistenceService;
	private final NotificationService notificationService;
	private final ClientManager clientManager;
	private final AuthenticationManager authenticationManager;
	private final SessionManager sessionManager;
	private final AuthorizationManager authorizationManager;
	private final FeedbackMailFactory feedbackMailFactory;

	protected BusinessLogicImpl(final PersistenceService persistenceService, final NotificationService notificationService, final ClientManager clientManager,
			final AuthenticationManager authenticationManager, final AuthorizationManager authorizationManager, final SessionManager sessionManager,
			final FeedbackMailFactory userQuotaRequestMailFactory) {
		this.persistenceService = persistenceService;
		this.notificationService = notificationService;
		this.clientManager = clientManager;
		this.authenticationManager = authenticationManager;
		this.authorizationManager = authorizationManager;
		this.sessionManager = sessionManager;
		this.feedbackMailFactory = userQuotaRequestMailFactory;
	}

	@Override
	public void handleIncomingActionSyncRequest(final ModelActionSyncRequest modelActionSyncRequest) throws UnableToHandleActionException,
			AuthorizationException {
		LOGGER.debug("Processing incoming action batch.");
		try {
			authorizationManager.assureProjectAccessAuthorization(modelActionSyncRequest.getProjectId());

			final List<ModelAction> actionList = modelActionSyncRequest.getActionList();
			final UUID projectId = modelActionSyncRequest.getProjectId();
			synchronized (this) {
				final User authenticatedUser = authenticationManager.getAuthenticatedUser();
				final Date actionTimestamp = new Date();
				final ActionContext actionContext = new ActionContext(authenticatedUser, actionTimestamp);
				final ProjectContext projectContext = validateIncomingActions(projectId, actionList, actionContext);
				postProcessIncomingActions(projectContext, actionContext, actionList);
				persistenceService.persistActions(projectId, authenticatedUser.getId(), actionList, actionTimestamp);
				modelActionSyncRequest.setActionContext(actionContext);
			}
			notificationService.notifyActions(modelActionSyncRequest);
		}
		catch (final PersistenceException e) {
			final String errorMessage = "The server could not handle the incoming action correctly. The action could not be persisted.";
			LOGGER.error(errorMessage, e);
			throw new UnableToHandleActionException(errorMessage);
		}
	}

	// TODO Report errors as feedback for development.
	// TODO Re-think validation strategy as loading the project every time may be a performance bottleneck.
	// DECISION It is common sense that this validation is needed at this time (of development). Roberto thinks it should be a provisory solution, as it may be
	// a major performance bottleneck, but that the solution is good to ensure that BetaTesters are safe while the application matures. Rodrigo thinks it should
	// be permanent (but maybe passive of refactorings) to guarantee user safety in the long term.
	private ProjectContext validateIncomingActions(final UUID projectId, final List<ModelAction> actionList, final ActionContext actionContext)
			throws UnableToHandleActionException {
		LOGGER.debug("Validating action upon the project current state.");
		try {
			final Project project = loadProject(projectId);
			final ProjectContext context = new ProjectContext(project);
			for (final ModelAction action : actionList)
				ActionExecuter.executeAction(context, actionContext, action);
			return context;
		}
		catch (final UnableToCompleteActionException e) {
			final String errorMessage = "Unable to process action. The incoming action is invalid.";
			LOGGER.error(errorMessage, e);
			throw new InvalidIncomingAction(errorMessage);
		}
		catch (final UnableToLoadProjectException e) {
			final String errorMessage = "The server could not handle the incoming action. The action could not be validated because the project could not be loaded.";
			LOGGER.error(errorMessage, e);
			throw new UnableToHandleActionException(errorMessage);
		}
		catch (final Exception e) {
			final String errorMessage = "Unable to process action. An unknown problem occured.";
			LOGGER.error(errorMessage, e);
			throw new InvalidIncomingAction(errorMessage);
		}
	}

	// TODO Find a better way to post process actions. (Eg. ScopeDeclareProgressAction come from clients with its own time definitions, and this time stamps
	// should be standardized using the server time).
	private void postProcessIncomingActions(final ProjectContext context, final ActionContext actionContext, final List<ModelAction> actionList)
			throws UnableToHandleActionException {
		try {
			for (final ModelAction action : actionList) {
				if (action instanceof ScopeDeclareProgressAction) ((ScopeDeclareProgressAction) action).setTimestamp(actionContext.getTimestamp());
				if (action instanceof FileUploadAction) {
					persistenceService.persistOrUpdateFileRepresentation(context.findFileRepresentation(action.getReferenceId()));
				}
				// FIXME Merge this with postProcessing
				if (action instanceof AnnotationCreateAction) new AnnotationCreatePostProcessor().process(persistenceService, context, actionContext,
						(AnnotationCreateAction) action);
			}
		}
		catch (final Exception e) {
			LOGGER.error("Post-Processing of the actions failed.", e);
			throw new InvalidIncomingAction("Unable to post-process action. The incoming action is invalid.");
		}
	}

	@Override
	// TODO make this method transactional.
	public ProjectRepresentation createProject(final String projectName) throws UnableToCreateProjectRepresentation, PersistenceException,
			AuthorizationException {

		LOGGER.debug("Creating new project '" + projectName + "'.");
		final User authenticatedUser = authenticationManager.getAuthenticatedUser();
		authorizationManager.validateAndUpdateUserProjectCreationQuota(authenticatedUser);

		try {
			final ProjectRepresentation persistedProjectRepresentation = persistenceService.persistOrUpdateProjectRepresentation(new ProjectRepresentation(
					projectName));

			authorize(authenticatedUser.getEmail(), persistedProjectRepresentation.getId(), false);
			if (!authenticatedUser.getEmail().equals(DefaultAuthenticationCredentials.USER_EMAIL)) authorizationManager
					.authorizeAdmin(persistedProjectRepresentation);

			notificationService.notifyProjectCreation(authenticatedUser.getId(), persistedProjectRepresentation);

			return persistedProjectRepresentation;
		}
		catch (final Exception e) {
			final String errorMessage = "Unable to create project '" + projectName + "'.";
			LOGGER.error(errorMessage, e);
			throw new UnableToCreateProjectRepresentation(errorMessage);
		}
	}

	@Override
	public void authorize(final String userEmail, final UUID projectId, final boolean wasRequestedByTheUser) throws UnableToAuthorizeUserException,
			UnableToHandleActionException,
			AuthorizationException {
		authorizationManager.authorize(projectId, userEmail, wasRequestedByTheUser);
		LOGGER.debug("Authorized user '" + userEmail + "' to project '" + projectId.toStringRepresentation() + "'");
		final List<ModelAction> list = new ArrayList<ModelAction>();
		list.add(new TeamInviteAction(userEmail));
		final ModelActionSyncRequest request = new ModelActionSyncRequest(projectId, list).setShouldNotifyCurrentClient(wasRequestedByTheUser);
		handleIncomingActionSyncRequest(request);
	}

	@Override
	public List<ProjectRepresentation> retrieveCurrentUserProjectList() throws UnableToRetrieveProjectListException {
		final User user = authenticationManager.getAuthenticatedUser();
		LOGGER.debug("Retrieving authorized project list for user '" + user + "'.");
		try {
			return authorizationManager.listAuthorizedProjects(user);
		}
		catch (final Exception e) {
			final String errorMessage = "Unable to retrieve the current user project list.";
			LOGGER.error(errorMessage, e);
			throw new UnableToRetrieveProjectListException(errorMessage);
		}
	}

	@Override
	public synchronized Project loadProjectForClient(final ProjectContextRequest projectContextRequest) throws UnableToLoadProjectException,
			ProjectNotFoundException {
		final Project loadedProject = loadProject(projectContextRequest.getRequestedProjectId());
		clientManager.bindClientToProject(sessionManager.getCurrentSession().getThreadLocalClientId(), projectContextRequest.getRequestedProjectId());
		return loadedProject;
	}

	@Override
	public Project loadProject(final UUID projectId) throws ProjectNotFoundException, UnableToLoadProjectException {
		LOGGER.debug("Loading current state for project id '" + projectId + "'.");
		try {
			authorizationManager.assureProjectAccessAuthorization(projectId);

			final ProjectSnapshot snapshot = loadProjectSnapshot(projectId);
			final List<UserAction> actionList = persistenceService.retrieveActionsSince(projectId, snapshot.getLastAppliedActionId());

			Project project = snapshot.getProject();
			if (actionList.isEmpty()) return project;

			project = applyActionsToProject(project, actionList);
			updateProjectSnapshot(snapshot, project, actionList.get(actionList.size() - 1).getId());

			return project;
		}
		catch (final NoResultFoundException e) {
			final String errorMessage = "The project '" + projectId + "' is inexistent.";
			LOGGER.error(errorMessage, e);
			throw new ProjectNotFoundException(errorMessage);
		}
		catch (final PersistenceException e) {
			final String errorMessage = "The server could not load the project: A persistence exception occured.";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		}
		catch (final UnableToCompleteActionException e) {
			final String errorMessage = "The project state could not be correctly restored.";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		}
		catch (final AuthorizationException e) {
			final String errorMessage = "Access denied to project '" + projectId + "'";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		}
		catch (final Exception e) {
			final String errorMessage = "The server could not load the project: Unknown error.";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		}
	}

	private ProjectSnapshot loadProjectSnapshot(final UUID projectId) throws PersistenceException, UnableToLoadProjectException, NoResultFoundException {
		ProjectSnapshot snapshot;
		try {
			snapshot = persistenceService.retrieveProjectSnapshot(projectId);
		}
		catch (final NoResultFoundException e) {
			snapshot = createBlankProjectSnapshot(projectId);
		}
		return snapshot;
	}

	private ProjectSnapshot createBlankProjectSnapshot(final UUID projectId) throws UnableToLoadProjectException, NoResultFoundException, PersistenceException {
		try {
			final ProjectRepresentation projectRepresentation = persistenceService.retrieveProjectRepresentation(projectId);

			final Scope projectScope = new Scope(projectRepresentation.getName(), new UUID("0"));
			final Release projectRelease = new Release(projectRepresentation.getName(), new UUID("release0"));

			final ProjectSnapshot projectSnapshot = new ProjectSnapshot(new Project(projectRepresentation, projectScope,
					projectRelease), new Date());
			return projectSnapshot;
		}
		catch (final IOException e) {
			final String errorMessage = "It was not possible to create a blank project snapshot.";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		}
	}

	private void updateProjectSnapshot(final ProjectSnapshot snapshot, final Project project, final long lastAppliedActionId) throws IOException,
			PersistenceException {

		snapshot.setProject(project);
		snapshot.setTimestamp(new Date());
		snapshot.setLastAppliedActionId(lastAppliedActionId);

		persistenceService.persistProjectSnapshot(snapshot);
	}

	private Project applyActionsToProject(final Project project, final List<UserAction> actionList) throws UnableToCompleteActionException {
		final ProjectContext context = new ProjectContext(project);

		for (final UserAction action : actionList) {
			User user;
			try {
				user = persistenceService.retrieveUserById(action.getUserId());
				ActionExecuter.executeAction(context, new ActionContext(user, action.getTimestamp()),
						action.getModelAction());
				final ActionContext actionContext = new ActionContext(user, action.getTimestamp());
				postProcessIncomingActions(context, actionContext, Arrays.asList(action.getModelAction()));
			}
			catch (final Exception e) {
				LOGGER.error("Unable to apply action to project", e);
				throw new UnableToCompleteActionException(e);
			}
		}

		return project;
	}

	@Override
	public void sendProjectCreationQuotaRequestEmail() {
		feedbackMailFactory.createUserQuotaRequestMail()
				.currentUser(authenticationManager.getAuthenticatedUser().getEmail())
				.send();
	}

	@Override
	public void sendFeedbackEmail(final String feedbackMessage) {
		feedbackMailFactory.createSendFeedbackMail()
				.currentUser(authenticationManager.getAuthenticatedUser().getEmail())
				.feedbackMessage(feedbackMessage)
				.send();

	}

	@Override
	public void onFileUploadCompleted(final FileRepresentation fileRepresentation) throws UnableToHandleActionException, AuthorizationException {
		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(new FileUploadAction(fileRepresentation));

		final ModelActionSyncRequest modelActionSyncRequest = new ModelActionSyncRequest(fileRepresentation.getProjectId(), actionList);
		modelActionSyncRequest.setShouldNotifyCurrentClient(true);
		handleIncomingActionSyncRequest(modelActionSyncRequest);
	}
}
