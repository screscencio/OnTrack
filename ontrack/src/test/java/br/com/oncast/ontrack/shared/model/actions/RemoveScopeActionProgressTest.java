package br.com.oncast.ontrack.shared.model.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.TestReleaseFactory;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuterTestUtils;

public class RemoveScopeActionProgressTest {

	private Scope rootScope;
	private ProjectContext context;

	@Before
	public void setUp() {
		rootScope = ScopeTestUtils.getScope();
		context = new ProjectContext(new Project(rootScope, TestReleaseFactory.create("")));
	}

	@Test
	public void shouldRemoveTheProgressStateOfRemovedScope() throws UnableToCompleteActionException {
		final Scope removedScope = rootScope.getChild(1);
		removedScope.getProgress().setDescription("Done");
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertTrue(removedScope.getProgress().isDone());
		assertTrue(removedScope.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, removedScope.getProgress().getState());

		new ScopeRemoveAction(removedScope.getId()).execute(context);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertFalse(removedScope.getProgress().hasDeclared());
		assertFalse(removedScope.getProgress().isDone());
		assertEquals(ProgressState.NOT_STARTED, removedScope.getProgress().getState());
	}

	@Test
	public void shouldRemoveTheProgressStateOfRemovedScopeChildren() throws UnableToCompleteActionException {
		final Scope removedScope = rootScope.getChild(0);
		final Scope childWithDeclaredProgress = removedScope.getChild(0).getChild(0);
		childWithDeclaredProgress.getProgress().setDescription("Done");
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(childWithDeclaredProgress.getParent());

		assertTrue(childWithDeclaredProgress.getProgress().isDone());
		assertTrue(childWithDeclaredProgress.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, childWithDeclaredProgress.getProgress().getState());

		new ScopeRemoveAction(removedScope.getId()).execute(context);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertFalse(childWithDeclaredProgress.getProgress().hasDeclared());
		assertFalse(childWithDeclaredProgress.getProgress().isDone());
		assertEquals(ProgressState.NOT_STARTED, childWithDeclaredProgress.getProgress().getState());
	}

	@Test
	public void rollbackShouldGiveBackProgressStateToScope() throws UnableToCompleteActionException {
		final Scope removedScope = rootScope.getChild(1);
		removedScope.getProgress().setDescription("Done");
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertTrue(removedScope.getProgress().isDone());
		assertTrue(removedScope.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, removedScope.getProgress().getState());

		final ModelAction rollbackAction = new ScopeRemoveAction(removedScope.getId()).execute(context);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertEquals(2, rootScope.getChildren().size());
		assertFalse(removedScope.getProgress().hasDeclared());
		assertFalse(removedScope.getProgress().isDone());
		assertEquals(ProgressState.NOT_STARTED, removedScope.getProgress().getState());

		rollbackAction.execute(context);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertEquals(3, rootScope.getChildren().size());
		assertTrue(rootScope.getChild(1).getProgress().isDone());
		assertTrue(rootScope.getChild(1).getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, rootScope.getChild(1).getProgress().getState());
	}

	@Test
	public void rollbackShouldGiveBackProgressStateToRemovedScopeChildren() throws UnableToCompleteActionException {
		final Scope removedScope = rootScope.getChild(0);
		final Scope childWithDeclaredProgress = removedScope.getChild(0).getChild(0);
		childWithDeclaredProgress.getProgress().setDescription("Done");
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(childWithDeclaredProgress.getParent());

		assertTrue(childWithDeclaredProgress.getProgress().isDone());
		assertTrue(childWithDeclaredProgress.getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, childWithDeclaredProgress.getProgress().getState());

		final ModelAction rollbackAction = new ScopeRemoveAction(removedScope.getId()).execute(context);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertFalse(childWithDeclaredProgress.getProgress().hasDeclared());
		assertFalse(childWithDeclaredProgress.getProgress().isDone());
		assertEquals(ProgressState.NOT_STARTED, childWithDeclaredProgress.getProgress().getState());

		rollbackAction.execute(context);
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		assertTrue(rootScope.getChild(0).getChild(0).getChild(0).getProgress().isDone());
		assertTrue(rootScope.getChild(0).getChild(0).getChild(0).getProgress().hasDeclared());
		assertEquals(ProgressState.DONE, rootScope.getChild(0).getChild(0).getChild(0).getProgress().getState());
	}

	@Test
	public void shouldHandleProgressStateCorrectlyAfterMultipleUndosAndRedos() throws UnableToCompleteActionException {
		Scope removedScope = rootScope.getChild(1);
		removedScope.getProgress().setDescription("Done");
		ActionExecuterTestUtils.executeInferenceEnginesForTestingPurposes(rootScope);

		final ActionExecutionManager actionExecutionManager = new ActionExecutionManager(Mockito.mock(ActionExecutionListener.class));
		actionExecutionManager.doUserAction(new ScopeRemoveAction(removedScope.getId()), context);

		assertEquals(2, rootScope.getChildren().size());
		assertFalse(removedScope.getProgress().hasDeclared());
		assertFalse(removedScope.getProgress().isDone());
		assertEquals(ProgressState.NOT_STARTED, removedScope.getProgress().getState());
		for (int i = 0; i < 20; i++) {
			actionExecutionManager.undoUserAction(context);

			assertEquals(3, rootScope.getChildren().size());
			assertTrue(rootScope.getChild(1).getProgress().isDone());
			assertTrue(rootScope.getChild(1).getProgress().hasDeclared());
			assertEquals(ProgressState.DONE, rootScope.getChild(1).getProgress().getState());

			removedScope = rootScope.getChild(1);
			actionExecutionManager.redoUserAction(context);

			assertEquals(2, rootScope.getChildren().size());
			assertFalse(removedScope.getProgress().hasDeclared());
			assertFalse(removedScope.getProgress().isDone());
			assertEquals(ProgressState.NOT_STARTED, removedScope.getProgress().getState());
		}
	}

}
