package br.com.oncast.ontrack.shared.model.scope.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuterTestUtils;

public class MoveRightScopeActionTest {

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

	@Test(expected = UnableToCompleteActionException.class)
	public void rootCantbeMovedRight() throws UnableToCompleteActionException {
		new ScopeMoveRightAction(rootScope.getId()).execute(context);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void aScopeCantBeMovedIfDontHaveUpSibling() throws UnableToCompleteActionException {
		new ScopeMoveRightAction(firstChild.getId()).execute(context);
	}

	@Test
	public void aScopeMovedToRightMustChangeToChildOfUpSibling() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(firstChild, rootScope.getChildren().get(0));
		assertEquals(lastChild, rootScope.getChildren().get(1));

		new ScopeMoveRightAction(lastChild.getId()).execute(context);

		assertEquals(1, rootScope.getChildren().size());
		assertEquals(firstChild, rootScope.getChildren().get(0));
		assertEquals(lastChild, firstChild.getChildren().get(0));
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(firstChild, rootScope.getChildren().get(0));
		assertEquals(0, firstChild.getChildren().size());
		assertEquals(lastChild, rootScope.getChildren().get(1));

		final ScopeMoveRightAction moveRightScopeAction = new ScopeMoveRightAction(lastChild.getId());
		final ModelAction rollbackAction = moveRightScopeAction.execute(context);

		assertEquals(1, rootScope.getChildren().size());
		assertEquals(1, firstChild.getChildren().size());
		assertEquals(firstChild, rootScope.getChildren().get(0));
		assertEquals(lastChild, firstChild.getChildren().get(0));

		rollbackAction.execute(context);

		assertEquals(2, rootScope.getChildren().size());
		assertEquals(0, firstChild.getChildren().size());
		assertEquals(firstChild, rootScope.getChildren().get(0));
		assertEquals(lastChild, rootScope.getChildren().get(1));
	}

	@Test
	public void rollbackMustGiveBackTheProgressStateToOldParentIfItWasLeaf() throws UnableToCompleteActionException {
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
	public void redoMustRemoveTheProgressStateOfBranch() throws UnableToCompleteActionException {
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
}
