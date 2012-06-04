package br.com.oncast.ontrack.shared.model.action.scope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuterTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class ScopeMoveRightAction_ProgressRemovalTest {

	private Scope rootScope;
	private Scope firstChild;
	private Scope lastChild;
	private ProjectContext context;

	@Before
	public void setUp() {
		rootScope = new Scope("root");
		firstChild = new Scope("first");
		lastChild = new Scope("last");
		rootScope.add(firstChild);
		rootScope.add(lastChild);

		context = ProjectTestUtils.createProjectContext(rootScope, ReleaseFactoryTestUtil.create(""));
	}

	@Test
	public void shouldRemoveTheProgressStateOfNewParent() throws UnableToCompleteActionException {
		firstChild.getProgress().setDescription("Done");
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertTrue(firstChild.getProgress().isDone());
		assertTrue(firstChild.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, firstChild.getProgress().getState());

		final ScopeMoveRightAction moveRightScopeAction = new ScopeMoveRightAction(lastChild.getId());
		moveRightScopeAction.execute(context, Mockito.mock(ActionContext.class));
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertFalse(firstChild.getProgress().hasDeclared());
		assertFalse(firstChild.getProgress().isDone());
	}

	@Test
	public void rollbackShouldGiveBackTheProgressStateToOldParentIfItWasLeaf() throws UnableToCompleteActionException {
		firstChild.getProgress().setDescription("Done");
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertTrue(firstChild.getProgress().isDone());
		assertTrue(firstChild.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, firstChild.getProgress().getState());

		final ScopeMoveRightAction moveRightScopeAction = new ScopeMoveRightAction(lastChild.getId());
		final ModelAction rollbackAction = moveRightScopeAction.execute(context, Mockito.mock(ActionContext.class));
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertFalse(firstChild.getProgress().hasDeclared());
		assertFalse(firstChild.getProgress().isDone());

		rollbackAction.execute(context, Mockito.mock(ActionContext.class));
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertTrue(firstChild.getProgress().isDone());
		assertTrue(firstChild.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, firstChild.getProgress().getState());
	}

	@Test
	public void redoShouldRemoveTheProgressStateOfBranch() throws UnableToCompleteActionException {
		firstChild.getProgress().setDescription("Done");
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		final ScopeMoveRightAction moveRightScopeAction = new ScopeMoveRightAction(lastChild.getId());
		final ModelAction rollbackAction = moveRightScopeAction.execute(context, Mockito.mock(ActionContext.class));
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);
		assertFalse(firstChild.getProgress().hasDeclared());
		assertFalse(firstChild.getProgress().isDone());

		final ModelAction redoAction = rollbackAction.execute(context, Mockito.mock(ActionContext.class));
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);
		assertTrue(firstChild.getProgress().isDone());
		assertTrue(firstChild.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, firstChild.getProgress().getState());

		redoAction.execute(context, Mockito.mock(ActionContext.class));
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);
		assertFalse(firstChild.getProgress().hasDeclared());
		assertFalse(firstChild.getProgress().isDone());
	}

	@Test
	public void shouldHandleProgressStateCorrectlyAfterMultipleUndosAndRedos() throws UnableToCompleteActionException {
		firstChild.getProgress().setDescription("Done");
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		final ScopeMoveRightAction moveRightScopeAction = new ScopeMoveRightAction(lastChild.getId());
		final ActionExecutionManager actionExecutionManager = new ActionExecutionManager(Mockito.mock(ActionExecutionListener.class));
		actionExecutionManager.doUserAction(moveRightScopeAction, context, Mockito.mock(ActionContext.class));
		for (int i = 0; i < 20; i++) {
			actionExecutionManager.undoUserAction(context, Mockito.mock(ActionContext.class));
			actionExecutionManager.redoUserAction(context, Mockito.mock(ActionContext.class));
		}
		assertFalse(firstChild.getProgress().hasDeclared());
		assertFalse(firstChild.getProgress().isDone());

		actionExecutionManager.undoUserAction(context, Mockito.mock(ActionContext.class));

		assertTrue(firstChild.getProgress().isDone());
		assertTrue(firstChild.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, firstChild.getProgress().getState());
	}
}
