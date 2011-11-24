package br.com.oncast.ontrack.server.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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

import br.com.oncast.ontrack.mocks.actions.ActionMock;
import br.com.oncast.ontrack.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.server.services.broadcast.BroadcastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.InvalidIncomingAction;
import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToCreateProjectRepresentation;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;

public class BusinessLogicTest {

	private static final int PROJECT_ID = 1;
	private EntityManager entityManager;
	private ProjectRepresentation projectRepresentation;

	@Before
	public void setUp() throws Exception {
		entityManager = Persistence.createEntityManagerFactory("ontrackPU").createEntityManager();
		projectRepresentation = assureProjectRepresentationExistance(PROJECT_ID);
	}

	@After
	public void tearDown() {
		entityManager.close();
	}

	@Test(expected = InvalidIncomingAction.class)
	public void shouldThrowExceptionWhenAnInvalidActionIsExecuted() throws UnableToHandleActionException {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithDumbPersistenceMockAndDumbBroadcastMock();
		final ArrayList<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(new ScopeUpdateAction(new UUID("id"), "bllla"));
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));
	}

	@Test(expected = InvalidIncomingAction.class)
	public void shouldThrowExceptionAndNotPersistWhenAnInvalidActionIsExecuted() throws UnableToHandleActionException {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithDumbNonWritablePersistenceMockAndDumbBroadcastMock();
		final ArrayList<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(new ScopeMoveUpAction(new UUID("0")));
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));
	}

	@Test
	public void shouldConstructAScopeHierarchyFromActions() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final Project project = ProjectTestUtils.createProject();
		final ProjectContext context = new ProjectContext(project);

		for (final ModelAction action : ActionMock.getActions()) {
			ActionExecuter.executeAction(context, action);
		}

		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(ActionMock.getActions()));
		final Scope projectScope = loadProject(business).getProjectScope();

		DeepEqualityTestUtils.assertObjectEquality(project.getProjectScope(), projectScope);
	}

	/**
	 * The purpose of this test is to execute actions in both client and server sides. This test does not assert anything, but it is useful for checking actions
	 * being executed one after another and the conversion of actions into entities and vice-versa.
	 */
	@Test
	public void shouldPersistActionsAndTheirRollbacks() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final Project project = ProjectTestUtils.createProject();
		final ProjectContext context = new ProjectContext(project);

		final List<ModelAction> rollbackActions = new ArrayList<ModelAction>();
		final List<ModelAction> actions = ActionMock.getActions();
		for (final ModelAction action : actions) {
			rollbackActions.add(ActionExecuter.executeAction(context, action).getReverseAction());
		}

		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actions));

		Collections.reverse(rollbackActions);
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(rollbackActions));
	}

	@Test
	public void loadProjectShouldGetEarliestSnapshotAndExecuteOnePendentAction() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final Project project1 = loadProject(business);

		final ModelAction action = new ScopeInsertChildAction(project1.getProjectScope().getId(), "big son");
		action.execute(new ProjectContext(project1));

		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(action);
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		final Project project2 = loadProject(business);

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);
	}

	@Test
	public void loadProjectShouldGetEarliestSnapshotAndExecuteTwoPendentActions() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final Project project1 = loadProject(business);

		final ModelAction action1 = new ScopeInsertChildAction(project1.getProjectScope().getId(), "big son");
		final ProjectContext context = new ProjectContext(project1);
		action1.execute(context);

		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(action1);

		final ModelAction action2 = new ScopeInsertChildAction(project1.getProjectScope().getId(), "small sister");
		action2.execute(context);
		actionList.add(action2);

		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		final Project project2 = loadProject(business);

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);
	}

	@Test
	public void loadProjectShouldGetEarliestSnapshotAndExecuteManyPendentActions() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final Project project1 = loadProject(business);

		final ProjectContext context = new ProjectContext(project1);
		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.addAll(ActionMock.getActions());

		for (final ModelAction action : actionList)
			action.execute(context);

		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		final Project project2 = loadProject(business);

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);
	}

	@Test
	public void loadProjectShouldGetEarliestSnapshotAndExecuteManyPendentActions2() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final Project project1 = loadProject(business);

		final List<ModelAction> actionList = executeActionsToProject(project1, ActionMock.getActions());
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		final Project project2 = loadProject(business);

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);

		final List<ModelAction> actionList2 = executeActionsToProject(project1, ActionMock.getActions2());
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList2));

		final Project project3 = loadProject(business);

		DeepEqualityTestUtils.assertObjectEquality(project1, project3);
	}

	@Test
	public void actionShouldNotUpdateOthersThanRelatedProject() throws Exception {
		final long OTHER_PROJECT_ID = 2;
		assureProjectRepresentationExistance(OTHER_PROJECT_ID);

		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final Project project1 = loadProject(business);

		final ScopeInsertChildAction action = new ScopeInsertChildAction(project1.getProjectScope().getId(), "big son");
		action.execute(new ProjectContext(project1));

		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(action);
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		final Project updatedProject = business.loadProject(PROJECT_ID);
		final Project notUpdatedProject = business.loadProject(OTHER_PROJECT_ID);

		assertEquals(updatedProject.getProjectScope().getChild(0).getId(), action.getNewScopeId());
		assertTrue(notUpdatedProject.getProjectScope().getChildren().isEmpty());
	}

	@Test
	public void actionShouldOnlyUpdateRelatedProject() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();

		final Project project1 = loadProject(business);
		final List<ModelAction> actionList = executeActionsToProject(project1, ActionMock.getActions());
		business.handleIncomingActionSyncRequest(createModelActionSyncRequest(actionList));

		final long OTHER_PROJECT_ID = 2;
		final ProjectRepresentation projectRepresentation2 = assureProjectRepresentationExistance(OTHER_PROJECT_ID);

		final Project project2 = business.loadProject(OTHER_PROJECT_ID);
		final List<ModelAction> actionList2 = executeActionsToProject(project2, ActionMock.getActions2());
		business.handleIncomingActionSyncRequest(new ModelActionSyncRequest(new UUID(), projectRepresentation2, actionList2));

		final Project loadedProject1 = loadProject(business);
		final Project loadedProject2 = business.loadProject(OTHER_PROJECT_ID);
		DeepEqualityTestUtils.assertObjectEquality(project1, loadedProject1);
		DeepEqualityTestUtils.assertObjectEquality(project2, loadedProject2);
	}

	@Test
	public void shouldLoadProjectWithGivenId() throws Exception {
		assureProjectRepresentationExistance(1);
		assureProjectRepresentationExistance(2);
		assureProjectRepresentationExistance(3);

		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final Project loadedProject = business.loadProject(2);

		assertEquals(2, loadedProject.getProjectRepresentation().getId());
		assertEquals("Default project", loadedProject.getProjectRepresentation().getName());
	}

	@Test(expected = ProjectNotFoundException.class)
	public void shouldNotLoadProjectIfInexistentIdIsGiven() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final long inexistentProjectId = 123;
		business.loadProject(inexistentProjectId);
	}

	@Test
	public void shouldCreateANewProjectRepresentation() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final long newProjectId = 2;

		assertProjectDoesNotExists(business, newProjectId);

		business.createProject("Name");
		final Project loadedProject = business.loadProject(newProjectId);

		assertEquals("Name", loadedProject.getProjectRepresentation().getName());
		assertEquals(newProjectId, loadedProject.getProjectRepresentation().getId());

	}

	@Test
	public void theGivenIdIsIgnoredOnCreation() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final int givenId = 5;
		final int sequenciallyGeneratedId = PROJECT_ID + 1;
		final String projectRepresentationName = "Name";

		assertProjectDoesNotExists(business, sequenciallyGeneratedId);
		assertProjectDoesNotExists(business, givenId);

		business.createProject(projectRepresentationName);

		assertProjectDoesNotExists(business, givenId);

		final Project loadedProject = business.loadProject(sequenciallyGeneratedId);
		assertEquals(projectRepresentationName, loadedProject.getProjectRepresentation().getName());
		assertEquals(sequenciallyGeneratedId, loadedProject.getProjectRepresentation().getId());
	}

	@Test
	public void scopeDeclareProgressActionShouldHaveHisTimestampRessetedByTheServer() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final List<ModelAction> actionList = new ArrayList<ModelAction>();

		final Project project1 = loadProject(business);

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
	public void createProjectShouldBroadcastAProjectCreationEvent() throws UnableToCreateProjectRepresentation, PersistenceException {
		final ProjectRepresentation projectRepresentation = new ProjectRepresentation("bla");

		final BroadcastService broadcastService = mock(BroadcastService.class);
		final PersistenceService persistenceService = mock(PersistenceService.class);
		when(persistenceService.persistOrUpdateProjectRepresentation(projectRepresentation)).thenReturn(projectRepresentation);

		final BusinessLogic business = new BusinessLogicImpl(persistenceService, broadcastService);
		final ProjectRepresentation representation = business.createProject("bla");

		verify(broadcastService, times(1)).broadcastProjectCreation(representation);
	}

	private void assertProjectDoesNotExists(final BusinessLogic business, final long newProjectId) throws UnableToLoadProjectException {
		try {
			business.loadProject(newProjectId);
			fail();
		}
		catch (final ProjectNotFoundException e) {}
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
		return new ModelActionSyncRequest(new UUID(), projectRepresentation, actionList);
	}

	private Project loadProject(final BusinessLogic business) throws UnableToLoadProjectException, ProjectNotFoundException {
		return business.loadProject(PROJECT_ID);
	}

	private ProjectRepresentation assureProjectRepresentationExistance(final long projectId) throws Exception {
		final ProjectRepresentation newProjectRepresentation = new ProjectRepresentation(projectId, "Default project");
		BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock().createProject(newProjectRepresentation.getName());
		return newProjectRepresentation;
	}

}