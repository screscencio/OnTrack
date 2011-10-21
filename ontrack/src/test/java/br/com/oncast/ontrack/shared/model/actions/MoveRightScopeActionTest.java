package br.com.oncast.ontrack.shared.model.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionManager;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.TestReleaseFactory;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

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

		context = new ProjectContext(new Project(rootScope, TestReleaseFactory.create("")));
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
	public void shouldHandleScopeHierarchyCorrectlyAfterMultipleUndosAndRedos() throws UnableToCompleteActionException {
		final ScopeMoveRightAction moveRightScopeAction = new ScopeMoveRightAction(lastChild.getId());
		final ActionExecutionManager actionExecutionManager = new ActionExecutionManager(Mockito.mock(ActionExecutionListener.class));
		actionExecutionManager.doUserAction(moveRightScopeAction, context);

		for (int i = 0; i < 20; i++) {
			actionExecutionManager.undoUserAction(context);

			assertEquals(2, rootScope.getChildren().size());
			assertEquals(0, firstChild.getChildren().size());
			assertEquals(firstChild, rootScope.getChildren().get(0));
			assertEquals(lastChild, rootScope.getChildren().get(1));

			actionExecutionManager.redoUserAction(context);

			assertEquals(1, rootScope.getChildren().size());
			assertEquals(1, firstChild.getChildren().size());
			assertEquals(firstChild, rootScope.getChildren().get(0));
			assertEquals(lastChild, firstChild.getChildren().get(0));
		}
	}
}
