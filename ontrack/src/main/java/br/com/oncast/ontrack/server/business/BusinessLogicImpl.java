package br.com.oncast.ontrack.server.business;

import br.com.oncast.ontrack.server.business.actionPostProcessments.ActionPostProcessmentsInitializer;
import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.actionPostProcessing.monitoring.DontPostProcessActions;
import br.com.oncast.ontrack.server.services.actionPostProcessing.monitoring.PostProcessActions;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.server.services.authentication.PasswordHash;
import br.com.oncast.ontrack.server.services.authorization.AuthorizationManager;
import br.com.oncast.ontrack.server.services.email.MailFactory;
import br.com.oncast.ontrack.server.services.integration.IntegrationService;
import br.com.oncast.ontrack.server.services.multicast.ClientManager;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.session.Session;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.server.services.threadSync.SyncronizationService;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToAuthorizeUserException;
import br.com.oncast.ontrack.shared.exceptions.authorization.UnableToRemoveAuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.business.InvalidIncomingAction;
import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateProjectRepresentationException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRemoveProjectException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToRetrieveProjectListException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.FileUploadAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.action.TeamRevogueInvitationAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.project.ProjectRevision;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.Profile;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;
import br.com.oncast.ontrack.shared.services.actionSync.ModelActionSyncEvent;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncEventRequestResponse;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.user.UserInformationUpdateEvent;
import br.com.oncast.ontrack.shared.utils.PrettyPrinter;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

class BusinessLogicImpl implements BusinessLogic {

	private static final int PROJECT_SNAPSHOT_UPDATE_ACTION_LIMIT = 50;

	private static final Logger LOGGER = Logger.getLogger(BusinessLogicImpl.class);

	private final PersistenceService persistenceService;
	private final MulticastService multicastService;
	private final ClientManager clientManager;
	private final AuthenticationManager authenticationManager;
	private final SessionManager sessionManager;
	private final AuthorizationManager authorizationManager;
	private final MailFactory mailFactory;
	private final IntegrationService integrationService;

	private final SyncronizationService syncronizationService;
	private final ActionPostProcessmentsInitializer postProcessmentsControler;

	protected BusinessLogicImpl(final PersistenceService persistenceService, final MulticastService multicastService, final ClientManager clientManager,
			final AuthenticationManager authenticationManager, final AuthorizationManager authorizationManager, final SessionManager sessionManager, final MailFactory mailFactory,
			final SyncronizationService syncronizationService, final ActionPostProcessmentsInitializer postProcessmentsControler, final IntegrationService integrationService) {
		this.persistenceService = persistenceService;
		this.multicastService = multicastService;
		this.clientManager = clientManager;
		this.authenticationManager = authenticationManager;
		this.authorizationManager = authorizationManager;
		this.sessionManager = sessionManager;
		this.mailFactory = mailFactory;
		this.syncronizationService = syncronizationService;
		this.postProcessmentsControler = postProcessmentsControler;
		this.integrationService = integrationService;
	}

	@Override
	@PostProcessActions
	public long handleIncomingActionSyncRequest(final ModelActionSyncRequest actionSyncRequest) throws UnableToHandleActionException, AuthorizationException {
		final long initialTime = getCurrentTime();
		try {
			final UUID projectId = actionSyncRequest.getProjectId();
			authorizationManager.assureActiveProjectAccessAuthorization(projectId);

			ModelActionSyncEvent modelActionSyncEvent = null;

			long lastApplyedActionId = -1;
			synchronized (syncronizationService.getSyncLockFor(projectId)) {
				final User authenticatedUser = authenticationManager.getAuthenticatedUser();
				final Date timestamp = new Date();

				final ActionContext actionContext = new ActionContext(authenticatedUser.getId(), timestamp);
				final List<ModelAction> actionList = actionSyncRequest.getActionList();

				validateIncomingActions(projectId, actionList, actionContext);
				lastApplyedActionId = persistenceService.persistActions(projectId, actionList, authenticatedUser.getId(), timestamp);
				modelActionSyncEvent = new ModelActionSyncEvent(projectId, actionList, actionContext, lastApplyedActionId);
				LOGGER.debug("Handled incoming actions " + PrettyPrinter.getSimpleNamesListString(actionList) + " from user " + authenticatedUser + " in " + getTimeSpent(initialTime) + " ms.");
			}
			if (actionSyncRequest.shouldReturnToSender()) multicastService.multicastToAllUsersInSpecificProject(modelActionSyncEvent, projectId);
			else multicastService.multicastToAllUsersButCurrentUserClientInSpecificProject(modelActionSyncEvent, projectId);
			return lastApplyedActionId;
		} catch (final PersistenceException e) {
			final String errorMessage = "The server could not handle the incoming action correctly. The action could not be persisted.";
			LOGGER.error(errorMessage, e);
			throw new UnableToHandleActionException(errorMessage);
		}
	}

	// TODO Report errors as feedback for development.
	// TODO Re-think validation strategy as loading the project every time may be a performance bottleneck.
	@PostProcessActions
	private void validateIncomingActions(final UUID projectId, final List<ModelAction> actionList, final ActionContext actionContext) throws UnableToHandleActionException {
		try {
			final ProjectRevision projectVersion = loadProject(projectId);
			final Project project = projectVersion.getProject();
			final ProjectContext context = new ProjectContext(project);
			for (final ModelAction action : actionList) {
				ActionExecuter.verifyPermissions(action, context, actionContext);
				ActionExecuter.executeAction(context, actionContext, action);
			}
		} catch (final UnableToCompleteActionException e) {
			final String errorMessage = "Unable to process action. The incoming action is invalid.";
			LOGGER.error(errorMessage, e);
			throw new InvalidIncomingAction(errorMessage);
		} catch (final UnableToLoadProjectException e) {
			final String errorMessage = "The server could not handle the incoming action. The action could not be validated because the project could not be loaded.";
			LOGGER.error(errorMessage, e);
			throw new UnableToHandleActionException(errorMessage);
		} catch (final Exception e) {
			final String errorMessage = "Unable to process action. An unknown problem occured.";
			LOGGER.error(errorMessage, e);
			throw new InvalidIncomingAction(errorMessage);
		}
	}

	private Project applyActionsToProject(final Project project, final List<UserAction> actionList) throws UnableToCompleteActionException {
		final ProjectContext projectContext = new ProjectContext(project);

		for (final UserAction action : actionList) {
			try {
				final User user = persistenceService.retrieveUserById(action.getUserId());
				final ActionContext actionContext = new ActionContext(user.getId(), action.getTimestamp());
				ActionExecuter.executeAction(projectContext, actionContext, action.getModelAction());
			} catch (final Exception e) {
				LOGGER.error("Unable to apply action to project", e);
				throw new UnableToCompleteActionException(action.getModelAction(), e);
			}
		}

		return project;
	}

	@Override
	public void removeAuthorization(final UUID userId, final UUID projectId) throws UnableToHandleActionException, UnableToRemoveAuthorizationException, AuthorizationException {
		authorizationManager.assureActiveProjectAccessAuthorization(projectId);

		final User authenticatedUser = authenticationManager.getAuthenticatedUser();
		if (!authenticatedUser.equals(userId)) authorizationManager.validateSuperUser(authenticatedUser.getId());
		handleIncomingActionSyncRequest(createModelActionSyncRequest(projectId, new TeamRevogueInvitationAction(userId)));

		if (userId.equals(DefaultAuthenticationCredentials.USER_ID)) return;

		authorizationManager.removeAuthorization(projectId, userId);
		LOGGER.debug("Removed authorization for user '" + userId + "' from project '" + projectId.toString() + "'");
	}

	@Override
	public void authorize(final String userEmail, final UUID projectId, final Profile profile, final boolean wasRequestedByTheUser) throws UnableToAuthorizeUserException,
			UnableToHandleActionException, AuthorizationException {
		final Profile projectProfile = profile.compareTo(Profile.PROJECT_MANAGER) > 0 ? Profile.PROJECT_MANAGER : profile;
		final UUID userId = authorizationManager.authorize(projectId, userEmail.toLowerCase().trim(), projectProfile, wasRequestedByTheUser);
		try {
			handleIncomingActionSyncRequest(createModelActionSyncRequest(projectId, new TeamInviteAction(userId, projectProfile)));
			LOGGER.debug("Authorized user '" + userEmail + "' to project '" + projectId.toString() + "'");
		} catch (final UnableToHandleActionException e) {
			try {
				authorizationManager.removeAuthorization(projectId, userId);
				LOGGER.error("Failed to authorize user '" + userEmail + "' to project '" + projectId.toString() + "'", e);
				throw e;
			} catch (final UnableToRemoveAuthorizationException e1) {
				LOGGER.error("Failed to rollback created authorization for user '" + userEmail + "' to project '" + projectId.toString() + "'", e1);
				throw new UnableToAuthorizeUserException("It was not possible to authorize user nor to roll it back", e1);
			}
		}
	}

	@Override
	public List<ProjectRepresentation> retrieveCurrentUserProjectList() throws UnableToRetrieveProjectListException {
		final UUID userId = authenticationManager.getAuthenticatedUser().getId();
		LOGGER.debug("Retrieving authorized project list for user '" + userId + "'.");
		try {
			final List<ProjectRepresentation> authorizedProjects = authorizationManager.listAuthorizedProjects(userId);
			for (final ProjectRepresentation project : new ArrayList<ProjectRepresentation>(authorizedProjects)) {
				if (project.removed()) authorizedProjects.remove(project);
			}
			return authorizedProjects;
		} catch (final Exception e) {
			final String errorMessage = "Unable to retrieve the current user project list.";
			LOGGER.error(errorMessage, e);
			throw new UnableToRetrieveProjectListException(errorMessage);
		}
	}

	@Override
	// TODO make this method transactional.
	public ProjectRepresentation createProject(final String projectName) throws UnableToCreateProjectRepresentationException {
		LOGGER.debug("Creating new project '" + projectName + "'.");
		final User authenticatedUser = authenticationManager.getAuthenticatedUser();

		try {
			authorizationManager.validateSuperUser(authenticatedUser.getId());
			final ProjectRepresentation persistedProjectRepresentation = persistenceService.persistOrUpdateProjectRepresentation(new ProjectRepresentation(projectName));
			integrationService.onProjectCreated(persistedProjectRepresentation, authenticatedUser);

			authorize(authenticatedUser.getEmail(), persistedProjectRepresentation.getId(), authenticatedUser.getGlobalProfile(), false);
			if (!authenticatedUser.getId().equals(DefaultAuthenticationCredentials.USER_ID)) authorizationManager.authorizeAdmin(persistedProjectRepresentation);

			return persistedProjectRepresentation;
		} catch (final Exception e) {
			final String errorMessage = "Unable to create project '" + projectName + "'.";
			LOGGER.error(errorMessage, e);
			throw new UnableToCreateProjectRepresentationException(errorMessage);
		}
	}

	@Override
	public ProjectRepresentation removeProject(final UUID projectId) throws UnableToRemoveProjectException {
		try {
			authorizationManager.assureActiveProjectAccessAuthorization(projectId);
			final ProjectRepresentation project = persistenceService.retrieveProjectRepresentation(projectId);
			project.setRemoved(true);
			persistenceService.persistOrUpdateProjectRepresentation(project);
			return project;
		} catch (final Exception e) {
			final String errorMessage = "Unable to remove project with id '" + projectId + "'.";
			LOGGER.error(errorMessage, e);
			throw new UnableToRemoveProjectException(errorMessage);
		}
	}

	@Override
	public synchronized ProjectRevision loadProjectForClient(final ProjectContextRequest projectContextRequest) throws UnableToLoadProjectException, ProjectNotFoundException {
		final long initialTime = getCurrentTime();

		final ProjectRevision loadedProject = loadProject(projectContextRequest.getRequestedProjectId());
		final Session currentSession = sessionManager.getCurrentSession();
		clientManager.bindClientToProject(currentSession.getThreadLocalClientId(), projectContextRequest.getRequestedProjectId());

		LOGGER.debug("Loaded project '" + loadedProject.getProject().getProjectRepresentation() + "' for user '" + currentSession.getAuthenticatedUser() + " ("
				+ currentSession.getThreadLocalClientId() + ")' in " + getTimeSpent(initialTime) + " ms.");
		return loadedProject;
	}

	@Override
	@DontPostProcessActions
	public ProjectRevision loadProject(final UUID projectId) throws ProjectNotFoundException, UnableToLoadProjectException {
		try {
			authorizationManager.assureActiveProjectAccessAuthorization(projectId);
			return doLoadProject(projectId);
		} catch (final AuthorizationException e) {
			final String errorMessage = "Access denied to project '" + projectId + "'";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		}
	}

	private ProjectRevision doLoadProject(final UUID projectId) throws ProjectNotFoundException, UnableToLoadProjectException {
		try {
			final ProjectSnapshot snapshot = loadProjectSnapshot(projectId);
			final List<UserAction> actionList = persistenceService.retrieveActionsSince(projectId, snapshot.getLastAppliedActionId());

			Project project = snapshot.getProject();
			if (actionList.isEmpty()) return new ProjectRevision(project, snapshot.getLastAppliedActionId());

			project = applyActionsToProject(project, actionList);

			final long lastAppliedActionId = (actionList.size() > 0) ? actionList.get(actionList.size() - 1).getId() : snapshot.getLastAppliedActionId();
			if (actionList.size() > PROJECT_SNAPSHOT_UPDATE_ACTION_LIMIT) {
				updateProjectSnapshot(snapshot, project, lastAppliedActionId);
			}

			return new ProjectRevision(project, lastAppliedActionId);
		} catch (final NoResultFoundException e) {
			final String errorMessage = "The project '" + projectId + "' is inexistent.";
			LOGGER.error(errorMessage, e);
			throw new ProjectNotFoundException(errorMessage);
		} catch (final PersistenceException e) {
			final String errorMessage = "The server could not load the project: A persistence exception occured.";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		} catch (final UnableToCompleteActionException e) {
			final String errorMessage = "The project state could not be correctly restored.";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		} catch (final Exception e) {
			final String errorMessage = "The server could not load the project: Unknown error.";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		}
	}

	private ProjectSnapshot loadProjectSnapshot(final UUID projectId) throws PersistenceException, UnableToLoadProjectException, NoResultFoundException {
		ProjectSnapshot snapshot;
		try {
			snapshot = persistenceService.retrieveProjectSnapshot(projectId);
		} catch (final NoResultFoundException e) {
			snapshot = createBlankProjectSnapshot(projectId);
		}
		return snapshot;
	}

	private ProjectSnapshot createBlankProjectSnapshot(final UUID projectId) throws UnableToLoadProjectException, NoResultFoundException, PersistenceException {
		try {
			final ProjectRepresentation projectRepresentation = persistenceService.retrieveProjectRepresentation(projectId);

			final User authenticatedUser = authenticationManager.getAuthenticatedUser();
			final UUID authenticatedUserId = authenticatedUser == null ? DefaultAuthenticationCredentials.USER_ID : authenticatedUser.getId();

			final Scope projectScope = new Scope(projectRepresentation.getName(), new UUID("0"), new UserRepresentation(authenticatedUserId), new Date(0));
			final Release projectRelease = new Release(projectRepresentation.getName(), new UUID("release0"));

			final ProjectSnapshot projectSnapshot = new ProjectSnapshot(new Project(projectRepresentation, projectScope, projectRelease), new Date());
			return projectSnapshot;
		} catch (final IOException e) {
			final String errorMessage = "It was not possible to create a blank project snapshot.";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		}
	}

	private void updateProjectSnapshot(final ProjectSnapshot snapshot, final Project project, final long lastAppliedActionId) throws IOException, PersistenceException {

		snapshot.setProject(project);
		snapshot.setTimestamp(new Date());
		snapshot.setLastAppliedActionId(lastAppliedActionId);

		persistenceService.persistProjectSnapshot(snapshot);
	}

	@Override
	public void sendProjectCreationQuotaRequestEmail() {
		mailFactory.createUserQuotaRequestMail().currentUser(authenticationManager.getAuthenticatedUser().getEmail()).send();
	}

	@Override
	public void sendFeedbackEmail(final String feedbackMessage) {
		mailFactory.createSendFeedbackMail().currentUser(authenticationManager.getAuthenticatedUser().getEmail()).feedbackMessage(feedbackMessage).send();

	}

	@Override
	public void onFileUploadCompleted(final FileRepresentation fileRepresentation) throws UnableToHandleActionException, AuthorizationException {
		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(new FileUploadAction(fileRepresentation));
		handleIncomingActionSyncRequest(new ModelActionSyncRequest(fileRepresentation.getProjectId(), actionList));
	}

	@Override
	@PostProcessActions
	public void loadProjectForMigration(final UUID projectId) throws ProjectNotFoundException, UnableToLoadProjectException {
		postProcessmentsControler.getNotificationCreationPostProcessor().deactivate();
		postProcessmentsControler.getScopeBindIdPostProcessor().deactivate();
		doLoadProject(projectId);
		postProcessmentsControler.getNotificationCreationPostProcessor().activate();
		postProcessmentsControler.getScopeBindIdPostProcessor().activate();
	}

	private long getCurrentTime() {
		return new Date().getTime();
	}

	private long getTimeSpent(final long initialTime) {
		return getCurrentTime() - initialTime;
	}

	@Override
	public ModelActionSyncEventRequestResponse loadProjectActions(final UUID projectId, final long lastSyncId) throws AuthorizationException, UnableToLoadProjectException {
		try {
			authorizationManager.assureActiveProjectAccessAuthorization(projectId);

			final Session currentSession = sessionManager.getCurrentSession();
			clientManager.bindClientToProject(currentSession.getThreadLocalClientId(), projectId);

			final List<UserAction> userActionList = persistenceService.retrieveActionsSince(projectId, lastSyncId);
			final List<ModelAction> actionList = new ArrayList<ModelAction>();
			for (final UserAction userAction : userActionList)
				actionList.add(userAction.getModelAction());

			final long lastUserActionId = (userActionList.size() > 0) ? userActionList.get(userActionList.size() - 1).getId() : lastSyncId;
			final Date timestamp = (userActionList.size() > 0) ? userActionList.get(userActionList.size() - 1).getTimestamp() : new Date();
			final ActionContext actionContext = new ActionContext(authenticationManager.getAuthenticatedUser().getId(), timestamp);

			return new ModelActionSyncEventRequestResponse(new ModelActionSyncEvent(projectId, actionList, actionContext, lastUserActionId));
		} catch (final PersistenceException e) {
			final String errorMessage = "The server could not resync the project: A persistence exception occured.";
			LOGGER.error(errorMessage, e);
			throw new UnableToLoadProjectException(errorMessage);
		}
	}

	@Override
	public UUID createUser(final String userEmail, final Profile profile) {
		User user = retrieveExistingUser(userEmail);
		if (user != null) {
			if (!DefaultAuthenticationCredentials.USER_ID.equals(user.getId())) {
				updateGlobalProfile(user, profile);
			}
			return user.getId();
		}

		final String generatedPassword = generatePasswordForNewUser(userEmail);
		user = authenticationManager.createNewUser(userEmail, generatedPassword, profile);
		LOGGER.debug("Created New User '" + userEmail + "'.");
		sendWelcomeMail(userEmail, generatedPassword);
		return user.getId();

	}

	private void updateGlobalProfile(final User user, final Profile profile) {
		user.setGlobalProfile(profile);
		try {
			persistenceService.persistOrUpdateUser(user);
			multicastService.multicastToUser(new UserInformationUpdateEvent(user), user);
		} catch (final PersistenceException e) {
			final String message = "Could not update the global profile of the existing new user (" + user.getEmail() + ")";
			LOGGER.error(message, e);
			throw new RuntimeException(message);
		}
	}

	private ModelActionSyncRequest createModelActionSyncRequest(final UUID projectId, final ModelAction action) {
		return new ModelActionSyncRequest(projectId, Arrays.asList(new ModelAction[] { action }));
	}

	private void sendWelcomeMail(final String userEmail, final String generatedPassword) {
		try {
			mailFactory.createWelcomeMail().sendTo(userEmail, generatedPassword);
		} catch (final Exception e) {
			final String message = "The user was created but welcome e-mail was not sent.";
			LOGGER.error(message, e);
			throw new RuntimeException(message, e);
		}
	}

	private User retrieveExistingUser(final String userEmail) {
		try {
			return persistenceService.retrieveUserByEmail(userEmail);
		} catch (final NoResultFoundException e) {
			return null;
		} catch (final PersistenceException e) {
			final String message = "It was not possible to create new user '" + userEmail + "': check for existing user failed.";
			LOGGER.error(message);
			throw new RuntimeException(message, e);
		}
	}

	private String generatePasswordForNewUser(final String userEmail) {
		try {
			return PasswordHash.generatePassword();
		} catch (final NoSuchAlgorithmException e) {
			final String message = "It was not possible to create new user '" + userEmail + "': Password generation went wrong.";
			LOGGER.error(message);
			throw new RuntimeException(message, e);
		}
	}

}
