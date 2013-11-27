package br.com.oncast.ontrack.shared.model.action.scope;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.server.services.exportImport.xml.UserActionTestUtils;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuterTestUtils;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.when;

public class ScopeMoveRightAction_ProgressRemovalTest {

	private Scope rootScope;
	private Scope firstChild;
	private Scope lastChild;
	private ProjectContext context;

	@Mock
	private ActionContext actionContext;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(actionContext.getUserId()).thenReturn(UserTestUtils.getAdmin().getId());
		when(actionContext.getTimestamp()).thenReturn(new Date(0));

		rootScope = ScopeTestUtils.createScope("root");
		firstChild = ScopeTestUtils.createScope("first");
		lastChild = ScopeTestUtils.createScope("last");
		rootScope.add(firstChild);
		rootScope.add(lastChild);

		context = ProjectTestUtils.createProjectContext(rootScope, ReleaseTestUtils.createRelease(""));
	}

	@Test
	public void shouldRemoveTheProgressStateOfNewParent() throws UnableToCompleteActionException {
		ScopeTestUtils.setProgress(firstChild, "Done");
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertTrue(firstChild.getProgress().isDone());
		assertTrue(firstChild.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, firstChild.getProgress().getState());

		final ScopeMoveRightAction moveRightScopeAction = new ScopeMoveRightAction(lastChild.getId());
		moveRightScopeAction.execute(context, actionContext);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertFalse(firstChild.getProgress().hasDeclared());
		assertFalse(firstChild.getProgress().isDone());
	}

	@Test
	public void rollbackShouldGiveBackTheProgressStateToOldParentIfItWasLeaf() throws UnableToCompleteActionException {
		ScopeTestUtils.setProgress(firstChild, "Done");
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertTrue(firstChild.getProgress().isDone());
		assertTrue(firstChild.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, firstChild.getProgress().getState());

		final ScopeMoveRightAction moveRightScopeAction = new ScopeMoveRightAction(lastChild.getId());
		final ModelAction rollbackAction = moveRightScopeAction.execute(context, actionContext);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertFalse(firstChild.getProgress().hasDeclared());
		assertFalse(firstChild.getProgress().isDone());

		rollbackAction.execute(context, actionContext);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertTrue(firstChild.getProgress().isDone());
		assertTrue(firstChild.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, firstChild.getProgress().getState());
	}

	@Test
	public void redoShouldRemoveTheProgressStateOfBranch() throws UnableToCompleteActionException {
		ScopeTestUtils.setProgress(firstChild, "Done");
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		final ScopeMoveRightAction moveRightScopeAction = new ScopeMoveRightAction(lastChild.getId());
		final ModelAction rollbackAction = moveRightScopeAction.execute(context, actionContext);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);
		assertFalse(firstChild.getProgress().hasDeclared());
		assertFalse(firstChild.getProgress().isDone());

		final ModelAction redoAction = rollbackAction.execute(context, actionContext);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);
		assertTrue(firstChild.getProgress().isDone());
		assertTrue(firstChild.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, firstChild.getProgress().getState());

		redoAction.execute(context, actionContext);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);
		assertFalse(firstChild.getProgress().hasDeclared());
		assertFalse(firstChild.getProgress().isDone());
	}

	@Test
	public void shouldHandleProgressStateCorrectlyAfterMultipleUndosAndRedos() throws UnableToCompleteActionException {
		ScopeTestUtils.setProgress(firstChild, "Done");
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		final ScopeMoveRightAction moveRightScopeAction = new ScopeMoveRightAction(lastChild.getId());
		final ActionExecutionManager actionExecutionManager = ActionExecutionTestUtils.createManager(context);
		actionExecutionManager.doUserAction(UserActionTestUtils.create(moveRightScopeAction, actionContext));
		for (int i = 0; i < 20; i++) {
			actionExecutionManager.undoUserAction();
			actionExecutionManager.redoUserAction();
		}
		assertFalse(firstChild.getProgress().hasDeclared());
		assertFalse(firstChild.getProgress().isDone());

		actionExecutionManager.undoUserAction();

		assertTrue(firstChild.getProgress().isDone());
		assertTrue(firstChild.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, firstChild.getProgress().getState());
	}
}
