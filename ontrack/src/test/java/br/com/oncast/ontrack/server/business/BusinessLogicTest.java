package br.com.oncast.ontrack.server.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
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
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.server.services.notification.ClientManager;
import br.com.oncast.ontrack.server.services.notification.NotificationService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.ProjectAuthorization;
import br.com.oncast.ontrack.server.services.session.Session;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.shared.exceptions.authentication.AuthorizationException;
import br.com.oncast.ontrack.shared.exceptions.business.InvalidIncomingAction;
import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateProjectRepresentation;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;
import br.com.oncast.ontrack.utils.mocks.actions.ActionTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;
import br.com.oncast.ontrack.utils.mocks.requests.RequestTestUtils;

public class BusinessLogicTest {

	private static final int PROJECT_ID = 1;
	private EntityManager entityManager;
	private ProjectRepresentation projectRepresentation;

	private BusinessLogic business;
	private PersistenceService persistence;
	private ClientManager clientManager;
	private AuthenticationManager authenticationManager;
	private NotificationService notification;
	private SessionManager sessionManager;
	private User authenticatedUser;
	private User admin;

	@Before
	public void setUp() throws Exception {
		entityManager = Persistence.createEntityManagerFactory("ontrackPU").createEntityManager();
		projectRepresentation = assureProjectRepresentationExistance(PROJECT_ID);

		configureMockDefaultBehavior();
	}

	private void configureMockDefaultBehavior() throws Exception {
		authenticationManager = mock(AuthenticationManager.class);
		persistence = mock(PersistenceService.class);
		clientManager = mock(ClientManager.class);
		notification = mock(NotificationService.class);
		sessionManager = mock(SessionManager.class);

		admin = UserTestUtils.createUser(DefaultAuthenticationCredentials.USER_EMAIL);
		authenticatedUser = UserTestUtils.createUser(100);
		configureToRetrieveAdmin();
		authorizeUser(authenticatedUser, PROJECT_ID);
		configureToRetrieveSnapshot(PROJECT_ID);
	}

	private void configureToRetrieveAdmin() throws NoResultFoundException, PersistenceException {
		when(persistence.retrieveUserByEmail(DefaultAuthenticationCredentials.USER_EMAIL)).thenReturn(admin);
	}

	private void authorizeUser(final User user, final long projectId) throws PersistenceException, NoResultFoundException {
		when(authenticationManager.getAuthenticatedUser()).thenReturn(user);
		final ProjectAuthorization authorization = mock(ProjectAuthorization.class);
		when(persistence.retrieveUserByEmail(user.getEmail())).thenReturn(user);
		when(persistence.retrieveProjectAuthorization(user.getId(), projectId)).thenReturn(authorization);
	}

	private void configureToRetrieveSnapshot(final int projectId) throws Exception {
		final ProjectSnapshot snapshot = mock(ProjectSnapshot.class);
		when(persistence.retrieveProjectSnapshot(projectId)).thenReturn(snapshot);
		when(snapshot.getProject()).thenReturn(ProjectTestUtils.createProject());
	}

	@After
	public void tearDown() {
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
		business = new BusinessLogicImpl(persistence, notification, clientManager, authenticationManager, sessionManager);

		final ArrayList<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(new ScopeMoveUpAction(new UUID("0")));

		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		verify(persistence, times(0)).persistActions(anyLong(), anyList(), any(Date.class));
	}

	@Test
	public void shouldConstructAScopeHierarchyFromActions() throws Exception {
		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final Project project = ProjectTestUtils.createProject();
		final ProjectContext context = new ProjectContext(project);

		for (final ModelAction action : ActionTestUtils.createSomeActions()) {
			ActionExecuter.executeAction(context, action);
		}

		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(ActionTestUtils.createSomeActions()));
		final Scope projectScope = loadProject().getProjectScope();

		DeepEqualityTestUtils.assertObjectEquality(project.getProjectScope(), projectScope);
	}

	/**
	 * The purpose of this test is to execute actions in both client and server sides. This test does not assert anything, but it is useful for checking actions
	 * being executed one after another and the conversion of actions into entities and vice-versa.
	 */
	@Test
	public void shouldPersistActionsAndTheirRollbacks() throws Exception {
		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final Project project = ProjectTestUtils.createProject();
		final ProjectContext context = new ProjectContext(project);

		final List<ModelAction> rollbackActions = new ArrayList<ModelAction>();
		final List<ModelAction> actions = ActionTestUtils.createSomeActions();
		for (final ModelAction action : actions) {
			rollbackActions.add(ActionExecuter.executeAction(context, action).getReverseAction());
		}

		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actions));

		Collections.reverse(rollbackActions);
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(rollbackActions));
	}

	@Test
	public void loadProjectShouldGetEarliestSnapshotAndExecuteOnePendentAction() throws Exception {
		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final Project project1 = loadProject();

		final ModelAction action = new ScopeInsertChildAction(project1.getProjectScope().getId(), "big son");
		action.execute(new ProjectContext(project1));

		final List<ModelAction> actionList = new ArrayList<ModelAction>();
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
		action1.execute(context);

		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(action1);

		final ModelAction action2 = new ScopeInsertChildAction(project1.getProjectScope().getId(), "small sister");
		action2.execute(context);
		actionList.add(action2);

		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		final Project project2 = loadProject();

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);
	}

	@Test
	public void loadProjectShouldGetEarliestSnapshotAndExecuteManyPendentActions() throws Exception {
		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final Project project1 = loadProject();

		final ProjectContext context = new ProjectContext(project1);
		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.addAll(ActionTestUtils.createSomeActions());

		for (final ModelAction action : actionList)
			action.execute(context);

		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		final Project project2 = loadProject();

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);
	}

	@Test
	public void loadProjectShouldGetEarliestSnapshotAndExecuteManyPendentActions2() throws Exception {
		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final Project project1 = loadProject();

		final List<ModelAction> actionList = executeActionsToProject(project1, ActionTestUtils.createSomeActions());
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
		final long OTHER_PROJECT_ID = 2;
		assureProjectRepresentationExistance(OTHER_PROJECT_ID);

		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final Project project1 = loadProject();

		final ScopeInsertChildAction action = new ScopeInsertChildAction(project1.getProjectScope().getId(), "big son");
		action.execute(new ProjectContext(project1));

		final List<ModelAction> actionList = new ArrayList<ModelAction>();
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
		final List<ModelAction> actionList = executeActionsToProject(project1, ActionTestUtils.createSomeActions());
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		final long OTHER_PROJECT_ID = 2;
		final ProjectRepresentation projectRepresentation2 = assureProjectRepresentationExistance(OTHER_PROJECT_ID);

		final Project project2 = loadProject(OTHER_PROJECT_ID);
		final List<ModelAction> actionList2 = executeActionsToProject(project2, ActionTestUtils.getActions2());
		business.handleIncomingActionSyncRequest(new ModelActionSyncRequest(projectRepresentation2, actionList2));

		final Project loadedProject1 = loadProject();
		final Project loadedProject2 = loadProject(OTHER_PROJECT_ID);
		DeepEqualityTestUtils.assertObjectEquality(project1, loadedProject1);
		DeepEqualityTestUtils.assertObjectEquality(project2, loadedProject2);
	}

	@Test
	public void shouldLoadProjectWithGivenId() throws Exception {
		assureProjectRepresentationExistance(1);
		assureProjectRepresentationExistance(2);
		assureProjectRepresentationExistance(3);

		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final Project loadedProject = loadProject(2);

		assertEquals(2, loadedProject.getProjectRepresentation().getId());
		assertEquals(ProjectTestUtils.DEFAULT_PROJECT_NAME, loadedProject.getProjectRepresentation().getName());
	}

	@Test(expected = ProjectNotFoundException.class)
	public void shouldNotLoadProjectIfInexistentIdIsGiven() throws Exception {
		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final long inexistentProjectId = 123;
		loadProject(inexistentProjectId);
	}

	@Test
	public void shouldCreateANewProjectRepresentation() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager);

		when(persistence.persistOrUpdateProjectRepresentation(Mockito.any(ProjectRepresentation.class))).thenReturn(ProjectTestUtils.createRepresentation(4));
		business.createProject("Name");

		final ArgumentCaptor<ProjectRepresentation> captor = ArgumentCaptor.forClass(ProjectRepresentation.class);
		verify(persistence).persistOrUpdateProjectRepresentation(captor.capture());

		final ProjectRepresentation createdProject = captor.getValue();
		assertEquals("Name", createdProject.getName());
	}

	@Test
	public void scopeDeclareProgressActionShouldHaveItsTimestampResetedByTheServer() throws Exception {
		business = BusinessLogicTestUtils.createWithJpaPersistence();
		final List<ModelAction> actionList = new ArrayList<ModelAction>();

		final Project project1 = loadProject();

		final ScopeDeclareProgressAction action = new ScopeDeclareProgressAction(project1.getProjectScope().getId(),
				Progress.ProgressState.DONE.getDescription());
		final Date givenTimestamp = new Date();
		action.setTimestamp(givenTimestamp);

		Thread.sleep(5);
		actionList.add(action);
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		assertFalse(givenTimestamp.equals(action.getTimestamp()));
	}

	@Test
	public void shouldSearchOnlyForProjectsThatAUserIsAuthorizedToAccess() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager);
		business.retrieveCurrentUserProjectList();

		verify(authenticationManager).getAuthenticatedUser();
		verify(persistence).retrieveProjectAuthorizations(authenticatedUser.getId());
	}

	@Test
	public void shouldAuthorizeCurrentUserAfterProjectCreation() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager);

		final ProjectRepresentation createdProject = ProjectTestUtils.createRepresentation(4);
		when(persistence.persistOrUpdateProjectRepresentation(any(ProjectRepresentation.class))).thenReturn(createdProject);

		business.createProject("new Project");
		verify(persistence).authorize(authenticatedUser.getEmail(), createdProject.getId());
	}

	@Test
	public void shouldAuthorizeAdminUserAfterProjectCreation() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager);

		final ProjectRepresentation createdProject = ProjectTestUtils.createRepresentation(4);
		when(persistence.persistOrUpdateProjectRepresentation(any(ProjectRepresentation.class))).thenReturn(createdProject);
		when(persistence.retrieveUserByEmail(authenticatedUser.getEmail())).thenReturn(authenticatedUser);

		business.createProject("new Project");
		verify(persistence).authorize(admin.getEmail(), createdProject.getId());
		verify(persistence).authorize(authenticatedUser.getEmail(), createdProject.getId());
	}

	@Test
	public void shouldBeAbleToAuthorizeAnExistentUser() throws Exception {
		final String mail = "user@mail.com";

		when(persistence.retrieveUserByEmail(mail)).thenReturn(UserTestUtils.createUser(mail));
		BusinessLogicTestUtils.create(persistence).authorize(PROJECT_ID, mail);
		verify(persistence).authorize(mail, PROJECT_ID);
	}

	@Test
	public void authorizeUserShouldCreateTheUserIfTheGivenUserDoesNotExist() throws Exception {
		business = BusinessLogicTestUtils.create(persistence);
		final String mail = "inexistent@mail.com";

		when(persistence.retrieveUserByEmail(mail)).thenThrow(new NoResultFoundException("", null));

		business.authorize(PROJECT_ID, mail);

		final ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(persistence).persistOrUpdateUser(captor.capture());

		assertEquals(mail, captor.getValue().getEmail());
	}

	@Test
	public void createProjectShouldNotifyAProjectCreation() throws UnableToCreateProjectRepresentation, PersistenceException, NoResultFoundException {
		when(persistence.persistOrUpdateProjectRepresentation(any(ProjectRepresentation.class))).thenReturn(ProjectTestUtils.createRepresentation(4));

		business = BusinessLogicTestUtils.create(persistence, notification, authenticationManager);
		final ProjectRepresentation representation = business.createProject("new project");

		verify(notification, times(1)).notifyProjectCreation(authenticatedUser.getId(), representation);
	}

	@Test
	public void bindClientToProjectAfterLoad() throws Exception {
		final UUID clientId = new UUID("123");

		projectRepresentation = ProjectTestUtils.createRepresentation();
		when(persistence.persistOrUpdateProjectRepresentation(projectRepresentation)).thenReturn(projectRepresentation);

		final Session sessionMock = Mockito.mock(Session.class);
		when(sessionManager.getCurrentSession()).thenReturn(sessionMock);
		when(sessionMock.getThreadLocalClientId()).thenReturn(clientId);

		business = BusinessLogicTestUtils.create(persistence, notification, clientManager, authenticationManager, sessionManager);

		final ProjectContextRequest request = new ProjectContextRequest(projectRepresentation.getId());
		business.loadProjectForClient(request);

		verify(clientManager, times(1)).bindClientToProject(clientId, request.getRequestedProjectId());
	}

	@Test(expected = AuthorizationException.class)
	public void notAuthorizedUserCannotExecuteActions() throws Exception {
		business = BusinessLogicTestUtils.create(persistence);
		final ModelActionSyncRequest request = RequestTestUtils.createModelActionSyncRequest();
		when(persistence.retrieveProjectAuthorization(authenticatedUser.getId(), request.getProjectId())).thenReturn(null);

		business.handleIncomingActionSyncRequest(request);
	}

	@Test
	public void onlyAuthorizedUserCanExecuteActions() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager);
		final ModelActionSyncRequest request = RequestTestUtils.createModelActionSyncRequestWithOneAction();

		business.handleIncomingActionSyncRequest(request);

		verify(persistence, atLeastOnce()).retrieveProjectAuthorization(authenticatedUser.getId(), request.getProjectId());
		verify(persistence).persistActions(eq(request.getProjectId()), eq(request.getActionList()), any(Date.class));
	}

	@Test
	public void onlyAuthorizedProjectsAreReturnedToUser() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager);

		final List<ProjectAuthorization> authorizations = ProjectTestUtils.createAuthorizations(3);
		when(persistence.retrieveProjectAuthorizations(authenticatedUser.getId())).thenReturn(authorizations);

		final List<ProjectRepresentation> projects = business.retrieveCurrentUserProjectList();

		assertEquals(projects.size(), authorizations.size());
		for (final ProjectAuthorization auth : authorizations) {
			assertTrue(projects.contains(auth.getProject()));
		}

		verify(persistence).retrieveProjectAuthorizations(authenticatedUser.getId());
	}

	@Test
	public void onlyAuthorizedUserCanLoadProjectForClient() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager, sessionManager);
		final ProjectContextRequest request = RequestTestUtils.createProjectContextRequest();

		final Session sessionMock = Mockito.mock(Session.class);
		when(sessionManager.getCurrentSession()).thenReturn(sessionMock);
		when(sessionMock.getThreadLocalClientId()).thenReturn(new UUID());

		business.loadProjectForClient(request);

		verify(persistence, atLeastOnce()).retrieveProjectAuthorization(authenticatedUser.getId(), request.getRequestedProjectId());
	}

	@Test(expected = UnableToLoadProjectException.class)
	public void notAuthorizedUserCannotLoadProjectForClient() throws Exception {
		business = BusinessLogicTestUtils.create(persistence, authenticationManager);

		final ProjectContextRequest request = RequestTestUtils.createProjectContextRequest();
		when(persistence.retrieveProjectAuthorization(authenticatedUser.getId(), request.getRequestedProjectId())).thenReturn(null);

		business.loadProjectForClient(request);
	}

	private List<ModelAction> executeActionsToProject(final Project project, final List<ModelAction> actions) throws UnableToCompleteActionException {
		final ProjectContext context = new ProjectContext(project);
		final List<ModelAction> actionList = new ArrayList<ModelAction>();

		for (final ModelAction action : actions) {
			actionList.add(action);
			action.execute(context);
		}
		return actionList;
	}

	private ModelActionSyncRequest createModelActionSyncRequest(final List<ModelAction> actionList) {
		return new ModelActionSyncRequest(projectRepresentation, actionList);
	}

	private Project loadProject() throws UnableToLoadProjectException, ProjectNotFoundException {
		return loadProject(PROJECT_ID);
	}

	private Project loadProject(final long projectId) throws UnableToLoadProjectException, ProjectNotFoundException {
		return business.loadProject(projectId);
	}

	private ProjectRepresentation assureProjectRepresentationExistance(final long projectId) throws Exception {
		final ProjectRepresentation newProjectRepresentation = ProjectTestUtils.createRepresentation(projectId);
		new PersistenceServiceJpaImpl().persistOrUpdateProjectRepresentation(newProjectRepresentation);
		return newProjectRepresentation;
	}
}