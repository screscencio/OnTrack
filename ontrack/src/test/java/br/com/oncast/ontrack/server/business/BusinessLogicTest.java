package br.com.oncast.ontrack.server.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.oncast.ontrack.server.business.actionPostProcessments.ActionPostProcessmentsInitializer;
import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessingService;
import br.com.oncast.ontrack.server.services.actionPostProcessing.ActionPostProcessor;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authorization.AuthorizationManager;
import br.com.oncast.ontrack.server.services.email.FeedbackMailFactory;
import br.com.oncast.ontrack.server.services.multicast.ClientManager;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.server.services.serverPush.CometClientConnection;
import br.com.oncast.ontrack.server.services.serverPush.ServerPushConnection;
import br.com.oncast.ontrack.server.services.session.Session;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.server.services.threadSync.SyncronizationService;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.business.InvalidIncomingAction;
import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateProjectRepresentation;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.FileUploadAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;
import br.com.oncast.ontrack.shared.services.context.ProjectCreatedEvent;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils;
import br.com.oncast.ontrack.utils.mocks.requests.ModelActionSyncTestUtils;
import br.com.oncast.ontrack.utils.model.FileRepresentationTestUtils;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

public class BusinessLogicTest {

	private static final UUID PROJECT_ID = new UUID();

	@Mock
	private PersistenceService persistence;

	@Mock
	private ClientManager clientManager;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private AuthorizationManager authorizationManager;

	@Mock
	private MulticastService multicast;

	@Mock
	private SessionManager sessionManager;

	@Mock
	private ActionContext actionContext;

	@Mock
	private ActionPostProcessmentsInitializer postProcessmentsInitializer;

	private EntityManager entityManager;
	private ProjectRepresentation projectRepresentation;
	private BusinessLogic business;
	private User authenticatedUser;
	private User admin;

	private Project project;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		admin = UserTestUtils.getAdmin();

		entityManager = Persistence.createEntityManagerFactory("ontrackPU").createEntityManager();
		projectRepresentation = assureProjectRepresentationExistance(PROJECT_ID);
		project = ProjectTestUtils.createProject();

		configureMockDefaultBehavior();

		when(actionContext.getUserId()).thenReturn(admin.getId());
		when(actionContext.getTimestamp()).thenReturn(new Date(0));
	}

	private void configureMockDefaultBehavior() throws Exception {
		authenticatedUser = UserTestUtils.createUser();
		configureToRetrieveAdmin();
		authenticateAndAuthorizeUser(authenticatedUser, PROJECT_ID);
		configureToRetrieveSnapshot(PROJECT_ID);

		when(authorizationManager.authorize(any(UUID.class), anyString(), anyBoolean())).thenAnswer(new Answer<User>() {
			@Override
			public User answer(final InvocationOnMock invocation) throws Throwable {
				return UserTestUtils.createUser((String) invocation.getArguments()[1]);
			}
		});
	}

	private void configureToRetrieveAdmin() throws NoResultFoundException, PersistenceException, UserNotFoundException {
		when(persistence.retrieveUserById(admin.getId())).thenReturn(admin);
		when(authenticationManager.findUserByEmail(admin.getEmail())).thenReturn(admin);
	}

	private void authenticateAndAuthorizeUser(final User user, final UUID projectId) throws PersistenceException, NoResultFoundException {
		when(authenticationManager.getAuthenticatedUser()).thenReturn(user);
		final ProjectAuthorization authorization = mock(ProjectAuthorization.class);
		when(persistence.retrieveUserById(user.getId())).thenReturn(user);
		when(persistence.retrieveProjectAuthorization(user.getId(), projectId)).thenReturn(authorization);

		project.addUser(user);
		when(actionContext.getUserId()).thenReturn(user.getId());
	}

	private void configureToRetrieveSnapshot(final UUID projectId) throws Exception {
		final ProjectSnapshot snapshot = mock(ProjectSnapshot.class);
		when(persistence.retrieveProjectSnapshot(projectId)).thenReturn(snapshot);
		when(snapshot.getProject()).thenReturn(project);
	}

	@After
	public void tearDown() {
		entityManager.clear();
		entityManager.close();
	}

	@Test(expected = InvalidIncomingAction.class)
	public void invalidActionThrowsException() throws Exception {
		business = BusinessLogicTestUtils.create();

		final ArrayList<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(new ScopeUpdateAction(new UUID("id"), "bllla"));

		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = InvalidIncomingAction.class)
	public void invalidActionIsNotPersisted() throws Exception {
		business = new BusinessLogicImpl(persistence, multicast, clientManager, authenticationManager,
				authorizationManager,
				sessionManager,
				mock(FeedbackMailFactory.class), new SyncronizationService(), postProcessmentsInitializer);

		final ArrayList<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(new ScopeMoveUpAction(UUID.INVALID_UUID));

		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		verify(persistence, times(0)).persistActions(any(UUID.class), anyList(), any(UUID.class), any(Date.class));
	}

	@Test
	public void shouldConstructAScopeHierarchyFromActions() throws Exception {
		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final Project project = ProjectTestUtils.createProject();
		final ProjectContext context = createContext(project);

		final List<ModelAction> someActions = createSomeActionsWithRequiredUsers();
		for (final ModelAction action : someActions) {
			ActionExecuter.executeAction(context, actionContext, action);
		}

		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(someActions));
		final Scope projectScope = loadProject().getProjectScope();

		DeepEqualityTestUtils.assertObjectEquality(project.getProjectScope(), projectScope);
	}

	private ProjectContext createContext(final Project project) {
		final ProjectContext context = new ProjectContext(project);
		return context;
	}

	/**
	 * The purpose of this test is to execute actions in both client and server sides. This test does not assert anything, but it is useful for checking actions
	 * being executed one after another and the conversion of actions into entities and vice-versa.
	 */
	@Test
	public void shouldPersistActionsAndTheirRollbacks() throws Exception {
		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final Project project = ProjectTestUtils.createProject();
		final ProjectContext context = createContext(project);

		final List<ModelAction> rollbackActions = new ArrayList<ModelAction>();
		final List<ModelAction> actions = createSomeActionsWithRequiredUsers();
		for (final ModelAction action : actions) {
			final ModelAction reverseAction = ActionExecuter.executeAction(context, actionContext, action).getReverseAction();
			if (reverseAction != null) rollbackActions.add(reverseAction);
		}

		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actions));

		Collections.reverse(rollbackActions);
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(rollbackActions));
	}

	private List<ModelAction> createSomeActionsWithRequiredUsers() {
		final List<ModelAction> actions = ActionTestUtils.createSomeActions(UserTestUtils.getAdmin(), authenticatedUser);
		return actions;
	}

	@Test
	public void loadProjectShouldGetEarliestSnapshotAndExecuteOnePendentAction() throws Exception {
		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final Project project1 = loadProject();

		final ModelAction action = new ScopeInsertChildAction(project1.getProjectScope().getId(), "big son");
		final ProjectContext context = new ProjectContext(project1);
		context.addUser(admin);
		action.execute(context, actionContext);

		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(new TeamInviteAction(admin));
		actionList.add(action);
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		final Project project2 = loadProject();

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);
	}

	@Test
	public void loadProjectShouldGetEarliestSnapshotAndExecuteTwoPendentActions() throws Exception {
		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final Project project1 = loadProject();

		final ModelAction action1 = new ScopeInsertChildAction(project1.getProjectScope().getId(), "big son");
		final ProjectContext context = new ProjectContext(project1);
		context.addUser(admin);
		action1.execute(context, actionContext);

		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(new TeamInviteAction(admin));
		actionList.add(action1);

		final ModelAction action2 = new ScopeInsertChildAction(project1.getProjectScope().getId(), "small sister");
		action2.execute(context, actionContext);
		actionList.add(action2);

		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		final Project project2 = loadProject();

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);
	}

	@Test
	public void loadProjectShouldGetEarliestSnapshotAndExecuteManyPendentActions() throws Exception {
		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final Project project1 = loadProject();

		final ProjectContext context = createContext(project1);
		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.addAll(createSomeActionsWithRequiredUsers());

		for (final ModelAction action : actionList)
			action.execute(context, actionContext);

		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		final Project project2 = loadProject();

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);
	}

	@Test
	public void loadProjectShouldGetEarliestSnapshotAndExecuteManyPendentActions2() throws Exception {
		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final Project project1 = loadProject();

		final List<ModelAction> actionList = executeActionsToProject(project1, createSomeActionsWithRequiredUsers());
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		final Project project2 = loadProject();

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);

		final List<ModelAction> actionList2 = executeActionsToProject(project1, ActionTestUtils.getActions2());
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList2));

		final Project project3 = loadProject();

		DeepEqualityTestUtils.assertObjectEquality(project1, project3);
	}

	@Test
	public void actionShouldNotUpdateOthersThanRelatedProject() throws Exception {
		final UUID OTHER_PROJECT_ID = new UUID();
		assureProjectRepresentationExistance(OTHER_PROJECT_ID);

		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final Project project1 = loadProject();

		final ScopeInsertChildAction action = new ScopeInsertChildAction(project1.getProjectScope().getId(), "big son");
		final ProjectContext context = new ProjectContext(project1);
		context.addUser(admin);
		action.execute(context, actionContext);

		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(new TeamInviteAction(admin));
		actionList.add(action);
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		final Project updatedProject = loadProject(PROJECT_ID);
		final Project notUpdatedProject = loadProject(OTHER_PROJECT_ID);

		assertEquals(updatedProject.getProjectScope().getChild(0).getId(), action.getNewScopeId());
		assertTrue(notUpdatedProject.getProjectScope().getChildren().isEmpty());
	}

	@Test
	public void actionShouldOnlyUpdateRelatedProject() throws Exception {
		business = BusinessLogicTestUtils.createWithJpaPersistence();

		final Project project1 = loadProject();
		final List<ModelAction> actionList = executeActionsToProject(project1, createSomeActionsWithRequiredUsers());
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		final UUID OTHER_PROJECT_ID = new UUID();
		final ProjectRepresentation projectRepresentation2 = assureProjectRepresentationExistance(OTHER_PROJECT_ID);

		final Project project2 = loadProject(OTHER_PROJECT_ID);
		final List<ModelAction> actions2 = ActionTestUtils.getActions2();
		actions2.add(0, new TeamInviteAction(admin));
		final List<ModelAction> actionList2 = executeActionsToProject(project2, actions2);
		business.handleIncomingActionSyncRequest(new ModelActionSyncRequest(projectRepresentation2, actionList2));

		final Project loadedProject1 = loadProject();
		final Project loadedProject2 = loadProject(OTHER_PROJECT_ID);
		DeepEqualityTestUtils.assertObjectEquality(project1, loadedProject1);
		DeepEqualityTestUtils.assertObjectEquality(project2, loadedProject2);
	}

	@Test
	public void shouldLoadProjectWithGivenId() throws Exception {
		final UUID projectId = new UUID();
		assureProjectRepresentationExistance(projectId);
		assureProjectRepresentationExistance(projectId);
		assureProjectRepresentationExistance(projectId);

		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final Project loadedProject = loadProject(projectId);

		assertEquals(projectId, loadedProject.getProjectRepresentation().getId());
		assertEquals(ProjectTestUtils.DEFAULT_PROJECT_NAME, loadedProject.getProjectRepresentation().getName());
	}

	@Test(expected = ProjectNotFoundException.class)
	public void shouldNotLoadProjectIfInexistentIdIsGiven() throws Exception {
		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final UUID inexistentProjectId = new UUID();
		loadProject(inexistentProjectId);
	}

	@Test
	public void shouldCreateANewProjectRepresentation() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager);

		final UUID projectId = new UUID();
		setupMocksToCreateProjectWithId(projectId);
		business.createProject("Name");

		final ArgumentCaptor<ProjectRepresentation> captor = ArgumentCaptor.forClass(ProjectRepresentation.class);
		verify(persistence).persistOrUpdateProjectRepresentation(captor.capture());

		final ProjectRepresentation createdProject = captor.getValue();
		assertEquals("Name", createdProject.getName());
	}

	@Test
	public void handleIncomingActionsShouldPostProcessActions() throws Exception {
		final ActionPostProcessingService postProcessingService = new ActionPostProcessingService();
		@SuppressWarnings("unchecked") final ActionPostProcessor<ScopeMoveLeftAction> postProcessor = mock(ActionPostProcessor.class);
		postProcessingService.registerPostProcessor(postProcessor, ScopeMoveLeftAction.class);

		business = BusinessLogicTestUtils.create(authenticationManager, authorizationManager, postProcessingService);

		final List<ModelAction> actions = createSomeActionsWithRequiredUsers();
		final ModelActionSyncRequest actionSyncRequest = new ModelActionSyncRequest(projectRepresentation, actions);

		business.handleIncomingActionSyncRequest(actionSyncRequest);

		verify(postProcessor, times(1)).process(any(ScopeMoveLeftAction.class), any(ActionContext.class), any(ProjectContext.class));
	}

	@Test
	public void createProjectShouldNotifyAProjectCreation() throws UnableToCreateProjectRepresentation, PersistenceException, NoResultFoundException,
			AuthorizationException {

		setupMocksToCreateProjectWithId(new UUID());
		business = BusinessLogicTestUtils.create(persistence, multicast, authenticationManager);
		final ProjectRepresentation representation = business.createProject("new project");

		final ArgumentCaptor<ProjectCreatedEvent> captor = ArgumentCaptor.forClass(ProjectCreatedEvent.class);
		verify(multicast, times(1)).multicastToUser(captor.capture(), eq(authenticatedUser));

		final ProjectCreatedEvent createdProject = captor.getValue();
		assertEquals(representation, createdProject.getProjectRepresentation());
	}

	@Test
	public void createProjectShouldFailIfUsersProjectCreationQuotaValidationFails() throws UnableToCreateProjectRepresentation, PersistenceException,
			AuthorizationException {
		doThrow(new AuthorizationException()).when(authorizationManager).validateAndUpdateUserProjectCreationQuota(authenticatedUser);
		try {
			BusinessLogicTestUtils.create(persistence, authenticationManager, authorizationManager).createProject("");
			Assert.fail("An authorization exception should have been thrown.");
		}
		catch (final Exception e) {}
		finally {
			verify(persistence, times(0)).persistOrUpdateProjectRepresentation(Mockito.any(ProjectRepresentation.class));
		}
	}

	@Test
	public void bindClientToProjectAfterLoad() throws Exception {
		final ServerPushConnection clientId = new CometClientConnection("1", "lesession");

		projectRepresentation = ProjectTestUtils.createRepresentation(PROJECT_ID);
		when(persistence.persistOrUpdateProjectRepresentation(projectRepresentation)).thenReturn(projectRepresentation);

		final Session sessionMock = Mockito.mock(Session.class);
		when(sessionManager.getCurrentSession()).thenReturn(sessionMock);
		when(sessionMock.getThreadLocalClientId()).thenReturn(clientId);

		business = BusinessLogicTestUtils.create(persistence, multicast, clientManager, authenticationManager, sessionManager);

		final ProjectContextRequest request = new ProjectContextRequest(projectRepresentation.getId());
		business.loadProjectForClient(request);

		verify(clientManager, times(1)).bindClientToProject(clientId, request.getRequestedProjectId());
	}

	@Test
	public void shouldAuthorizeCurrentUserAfterProjectCreation() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager, authorizationManager);

		final UUID projectId = new UUID();
		setupMocksToCreateProjectWithId(projectId);
		business.createProject("new Project");
		verify(authorizationManager).authorize(projectId, authenticatedUser.getEmail(), false);
	}

	@Test
	public void shouldAuthorizeAdminUserAfterProjectCreation() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager, authorizationManager);

		final ProjectRepresentation createdProject = setupMocksToCreateProjectWithId(new UUID());

		business.createProject("new Project");
		verify(authorizationManager).authorizeAdmin(createdProject);
		verify(authorizationManager).authorize(createdProject.getId(), authenticatedUser.getEmail(), false);
	}

	@Test(expected = AuthorizationException.class)
	public void notAuthorizedUserCannotExecuteActions() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authorizationManager);
		final ModelActionSyncRequest request = ModelActionSyncTestUtils.createModelActionSyncRequest();
		doThrow(new AuthorizationException()).when(authorizationManager).assureProjectAccessAuthorization(request.getProjectId());

		business.handleIncomingActionSyncRequest(request);
	}

	@Test
	public void onlyAuthorizedUserCanExecuteActions() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager, authorizationManager);
		final ModelActionSyncRequest request = ModelActionSyncTestUtils.createModelActionSyncRequestWithOneAction(PROJECT_ID);

		business.handleIncomingActionSyncRequest(request);

		verify(authorizationManager, atLeastOnce()).assureProjectAccessAuthorization(request.getProjectId());
		verify(persistence).persistActions(eq(request.getProjectId()), eq(request.getActionList()), eq(authenticatedUser.getId()),
				any(Date.class));
	}

	@Test
	public void onlyAuthorizedProjectsAreReturnedToUser() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager, authorizationManager);
		business.retrieveCurrentUserProjectList();
		verify(authorizationManager).listAuthorizedProjects(authenticatedUser);
	}

	@Test
	public void onlyAuthorizedUserCanLoadProjectForClient() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager, authorizationManager, sessionManager);
		final ProjectContextRequest request = ModelActionSyncTestUtils.createProjectContextRequest(PROJECT_ID);

		final Session sessionMock = Mockito.mock(Session.class);
		when(sessionManager.getCurrentSession()).thenReturn(sessionMock);
		when(sessionMock.getThreadLocalClientId()).thenReturn(new CometClientConnection("", ""));

		business.loadProjectForClient(request);

		verify(authorizationManager, atLeastOnce()).assureProjectAccessAuthorization(request.getRequestedProjectId());
	}

	@Test(expected = UnableToLoadProjectException.class)
	public void notAuthorizedUserCannotLoadProjectForClient() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager, authorizationManager);

		final ProjectContextRequest request = ModelActionSyncTestUtils.createProjectContextRequest();
		doThrow(new AuthorizationException()).when(authorizationManager).assureProjectAccessAuthorization(request.getRequestedProjectId());

		business.loadProjectForClient(request);
	}

	@Test
	public void actionsShouldBeBoundToAuthenticatedUser() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager, authorizationManager);

		final List<ModelAction> actions = createSomeActionsWithRequiredUsers();
		final ModelActionSyncRequest actionSyncRequest = new ModelActionSyncRequest(projectRepresentation, actions);
		business.handleIncomingActionSyncRequest(actionSyncRequest);

		verify(persistence).persistActions(eq(PROJECT_ID), eq(actions), eq(authenticatedUser.getId()), any(Date.class));
	}

	@Test
	public void actionsShouldBeBoundToAuthenticatedUser2() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager, authorizationManager);

		Mockito.reset(authenticationManager);

		final UUID userId = new UUID();
		final User createdUser = UserTestUtils.createUser(userId);
		final List<ModelAction> actions = ActionTestUtils.createSomeActions(UserTestUtils.getAdmin(), createdUser);
		final ModelActionSyncRequest actionSyncRequest = new ModelActionSyncRequest(projectRepresentation, actions);

		authenticateAndAuthorizeUser(createdUser, PROJECT_ID);

		business.handleIncomingActionSyncRequest(actionSyncRequest);

		verify(persistence).persistActions(eq(PROJECT_ID), eq(actions), eq(userId), any(Date.class));
	}

	@Test
	public void shouldCreateFileUploadActionWhenOnUploadCompletedWereCalled() throws Exception {
		final FileRepresentation fileRepresentation = FileRepresentationTestUtils.create();
		business = mock(BusinessLogicImpl.class);

		doCallRealMethod().when(business).onFileUploadCompleted(fileRepresentation);
		business.onFileUploadCompleted(fileRepresentation);

		final ArgumentCaptor<ModelActionSyncRequest> captor = ArgumentCaptor.forClass(ModelActionSyncRequest.class);
		verify(business).handleIncomingActionSyncRequest(captor.capture());

		final ModelActionSyncRequest request = captor.getValue();
		assertEquals(fileRepresentation.getProjectId(), request.getProjectId());
		final ModelAction action = request.getActionList().get(0);
		assertTrue(action instanceof FileUploadAction);
		assertEquals(fileRepresentation.getId(), action.getReferenceId());
	}

	private ProjectRepresentation setupMocksToCreateProjectWithId(final UUID projectId) throws PersistenceException, NoResultFoundException {
		final ProjectRepresentation projectRepresentation = ProjectTestUtils.createRepresentation(projectId);
		authenticateAndAuthorizeUser(authenticatedUser, projectId);

		when(persistence.retrieveProjectSnapshot(projectId)).thenThrow(new NoResultFoundException(null, null));
		when(persistence.persistOrUpdateProjectRepresentation(Mockito.any(ProjectRepresentation.class))).thenReturn(
				projectRepresentation);
		when(persistence.retrieveProjectRepresentation(projectId)).thenReturn(projectRepresentation);

		return projectRepresentation;
	}

	private List<ModelAction> executeActionsToProject(final Project project, final List<ModelAction> actions) throws UnableToCompleteActionException {
		final ProjectContext context = createContext(project);
		final List<ModelAction> actionList = new ArrayList<ModelAction>();

		for (final ModelAction action : actions) {
			actionList.add(action);
			action.execute(context, actionContext);
		}
		return actionList;
	}

	private ModelActionSyncRequest createModelActionSyncRequest(final List<ModelAction> actionList) {
		return new ModelActionSyncRequest(projectRepresentation, actionList);
	}

	private Project loadProject() throws UnableToLoadProjectException, ProjectNotFoundException {
		return loadProject(PROJECT_ID);
	}

	private Project loadProject(final UUID projectId) throws UnableToLoadProjectException, ProjectNotFoundException {
		return business.loadProject(projectId);
	}

	private ProjectRepresentation assureProjectRepresentationExistance(final UUID projectId) throws Exception {
		final ProjectRepresentation newProjectRepresentation = ProjectTestUtils.createRepresentation(projectId);
		new PersistenceServiceJpaImpl().persistOrUpdateProjectRepresentation(newProjectRepresentation);
		return newProjectRepresentation;
	}
}