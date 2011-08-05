package br.com.oncast.ontrack.shared.model.scope.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuterTestUtils;

public class MoveRightScopeActionProgressTest {

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

		context = new ProjectContext(new Project(rootScope, new Release("")));
	}

	@Test
	public void shouldRemoveTheProgressStateOfNewParent() throws UnableToCompleteActionException {
		firstChild.getProgress().setDescription("Done");
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertTrue(firstChild.getProgress().isDone());
		assertTrue(firstChild.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, firstChild.getProgress().getState());

		final ScopeMoveRightAction moveRightScopeAction = new ScopeMoveRightAction(lastChild.getId());
		moveRightScopeAction.execute(context);
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
		final ModelAction rollbackAction = moveRightScopeAction.execute(context);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertFalse(firstChild.getProgress().hasDeclared());
		assertFalse(firstChild.getProgress().isDone());

		rollbackAction.execute(context);
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
		final ModelAction rollbackAction = moveRightScopeAction.execute(context);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);
		assertFalse(firstChild.getProgress().hasDeclared());
		assertFalse(firstChild.getProgress().isDone());

		final ModelAction redoAction = rollbackAction.execute(context);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);
		assertTrue(firstChild.getProgress().isDone());
		assertTrue(firstChild.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, firstChild.getProgress().getState());

		redoAction.execute(context);
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
		actionExecutionManager.execute(moveRightScopeAction, context);
		for (int i = 0; i < 20; i++) {
			actionExecutionManager.undo(context);
			actionExecutionManager.redo(context);
		}
		assertFalse(firstChild.getProgress().hasDeclared());
		assertFalse(firstChild.getProgress().isDone());

		actionExecutionManager.undo(context);

		assertTrue(firstChild.getProgress().isDone());
		assertTrue(firstChild.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, firstChild.getProgress().getState());
	}
}
