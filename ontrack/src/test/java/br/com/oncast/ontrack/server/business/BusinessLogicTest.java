package br.com.oncast.ontrack.server.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.mocks.actions.ActionMock;
import br.com.oncast.ontrack.mocks.models.ProjectMock;
import br.com.oncast.ontrack.shared.exceptions.business.InvalidIncomingAction;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;

public class BusinessLogicTest {

	private EntityManager entityManager;

	@Before
	public void setUp() {
		entityManager = Persistence.createEntityManagerFactory("ontrackPU").createEntityManager();
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
		business.handleIncomingActionSyncRequest(new ModelActionSyncRequest(new UUID(), actionList));
	}

	@Test(expected = InvalidIncomingAction.class)
	public void shouldThrowExceptionAndNotPersistWhenAnInvalidActionIsExecuted() throws UnableToHandleActionException {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithDumbNonWritablePersistenceMockAndDumbBroadcastMock();
		final ArrayList<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(new ScopeMoveUpAction(new UUID("0")));
		business.handleIncomingActionSyncRequest(new ModelActionSyncRequest(new UUID(), actionList));
	}

	@Test
	public void shouldConstructAScopeHierarchyFromActions() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final Project project = ProjectMock.getProject();
		final ProjectContext context = new ProjectContext(project);

		for (final ModelAction action : ActionMock.getActions()) {
			ActionExecuter.executeAction(context, action);
		}

		business.handleIncomingActionSyncRequest(new ModelActionSyncRequest(new UUID(), ActionMock.getActions()));
		final Scope projectScope = business.loadProject().getProjectScope();

		DeepEqualityTestUtils.assertObjectEquality(project.getProjectScope(), projectScope);
	}

	/**
	 * The purpose of this test is to execute actions in both client and server sides. This test does not assert anything, but it is useful for checking actions
	 * being executed one after another and the conversion of actions into entities and vice-versa.
	 */
	@Test
	public void shouldPersistActionsAndTheirRollbacks() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final Project project = ProjectMock.getProject();
		final ProjectContext context = new ProjectContext(project);

		final List<ModelAction> rollbackActions = new ArrayList<ModelAction>();
		final List<ModelAction> actions = ActionMock.getActions();
		for (final ModelAction action : actions) {
			rollbackActions.add(ActionExecuter.executeAction(context, action).getReverseAction());
		}

		business.handleIncomingActionSyncRequest(new ModelActionSyncRequest(new UUID(), actions));

		Collections.reverse(rollbackActions);
		business.handleIncomingActionSyncRequest(new ModelActionSyncRequest(new UUID(), rollbackActions));
	}

	@Test
	public void shouldLoadProjectBuiltUponDefaultSnapshotAndOneAction() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final Project project1 = business.loadProject();

		final ModelAction action = new ScopeInsertChildAction(project1.getProjectScope().getId(), "big son");
		action.execute(new ProjectContext(project1));

		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(action);
		business.handleIncomingActionSyncRequest(new ModelActionSyncRequest(new UUID(), actionList));

		final Project project2 = business.loadProject();

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);
	}

	@Test
	public void shouldLoadProjectBuiltUponDefaultSnapshotAndTwoActions() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final Project project1 = business.loadProject();

		final ModelAction action1 = new ScopeInsertChildAction(project1.getProjectScope().getId(), "big son");
		final ProjectContext context = new ProjectContext(project1);
		action1.execute(context);

		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(action1);

		final ModelAction action2 = new ScopeInsertChildAction(project1.getProjectScope().getId(), "small sister");
		action2.execute(context);
		actionList.add(action2);

		business.handleIncomingActionSyncRequest(new ModelActionSyncRequest(new UUID(), actionList));

		final Project project2 = business.loadProject();

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);
	}

	@Test
	public void shouldLoadProjectBuiltUponDefaultSnapshotAndManyActions() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final Project project1 = business.loadProject();

		final ProjectContext context = new ProjectContext(project1);
		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.addAll(ActionMock.getActions());

		for (final ModelAction action : actionList)
			action.execute(context);

		business.handleIncomingActionSyncRequest(new ModelActionSyncRequest(new UUID(), actionList));

		final Project project2 = business.loadProject();

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);
	}

	@Test
	public void shouldLoadProjectBuiltUponDefaultSnapshotAndManyActions2() throws Exception {
		final BusinessLogic business = BusinessLogicMockFactoryTestUtils.createWithJpaPersistenceAndDumbBroadcastMock();
		final Project project1 = business.loadProject();

		final ProjectContext context = new ProjectContext(project1);
		final List<ModelAction> actionList = new ArrayList<ModelAction>();

		for (final ModelAction action : ActionMock.getActions()) {
			actionList.clear();
			actionList.add(action);

			action.execute(context);
			business.handleIncomingActionSyncRequest(new ModelActionSyncRequest(new UUID(), actionList));
		}

		final Project project2 = business.loadProject();

		DeepEqualityTestUtils.assertObjectEquality(project1, project2);

		actionList.clear();
		actionList.addAll(ActionMock.getActions2());
		for (final ModelAction action : actionList)
			action.execute(context);

		business.handleIncomingActionSyncRequest(new ModelActionSyncRequest(new UUID(), actionList));

		final Project project3 = business.loadProject();

		DeepEqualityTestUtils.assertObjectEquality(project1, project3);
	}

	// FIXME Test that incoming actions should be post-processed. Eg. SDPA TIMESTAMP should be re-set.
}