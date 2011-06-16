package br.com.oncast.ontrack.shared.scope.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.project.Project;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class MoveUpScopeActionTest {
	private Project project;
	private Scope rootScope;
	private Scope firstChild;
	private Scope lastChild;
	private ProjectContext context;

	@Before
	public void setUp() {
		rootScope = new Scope("root");
		firstChild = new Scope("child");
		lastChild = new Scope("last");
		rootScope.add(firstChild);
		rootScope.add(lastChild);

		project = new Project();
		project.setProjectScope(rootScope);
		project.setProjectRelease(new Release("Project"));

		context = new ProjectContext(project);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootCantbeMovedUp() throws UnableToCompleteActionException {
		new ScopeMoveUpAction(rootScope).execute(context);
	}

	@Test
	public void movedDownScopeMustBeDown() throws UnableToCompleteActionException {
		assertEquals(rootScope.getChildren().get(0), firstChild);
		assertEquals(rootScope.getChildren().get(1), lastChild);

		final ScopeMoveUpAction moveUp = new ScopeMoveUpAction(lastChild);
		moveUp.execute(context);

		assertEquals(rootScope.getChildren().get(0), lastChild);
		assertEquals(rootScope.getChildren().get(1), firstChild);
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(rootScope.getChildren().get(0), firstChild);
		assertEquals(rootScope.getChildren().get(1), lastChild);

		final ScopeMoveUpAction moveDown = new ScopeMoveUpAction(lastChild);
		moveDown.execute(context);

		assertEquals(rootScope.getChildren().get(0), lastChild);
		assertEquals(rootScope.getChildren().get(1), firstChild);

		moveDown.rollback(context);

		assertEquals(rootScope.getChildren().get(0), firstChild);
		assertEquals(rootScope.getChildren().get(1), lastChild);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void lastNodeCantBeMovedDown() throws UnableToCompleteActionException {
		new ScopeMoveUpAction(firstChild).execute(context);
	}
}
