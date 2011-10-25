package br.com.oncast.ontrack.shared.model.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;

public class MoveLeftScopeActionTest {

	private Scope rootScope;
	private Scope middle;
	private Scope lastChild;
	private ProjectContext context;

	@Before
	public void setUp() {
		rootScope = new Scope("root");
		middle = new Scope("middle");
		lastChild = new Scope("last");
		rootScope.add(middle);
		middle.add(lastChild);

		context = new ProjectContext(new Project(rootScope, ReleaseFactoryTestUtil.create("")));
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootCantbeMovedLeft() throws UnableToCompleteActionException {
		new ScopeMoveLeftAction(rootScope.getId()).execute(context);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void aRootChildCantbeMovedLeft() throws UnableToCompleteActionException {
		assertEquals(rootScope.getChildren().get(0), middle);
		new ScopeMoveLeftAction(middle.getId()).execute(context);
	}

	@Test
	public void aChildMovedLeftMustChangeToASibling() throws UnableToCompleteActionException {
		assertEquals(middle.getChildren().get(0), lastChild);
		assertEquals(1, middle.getChildren().size());
		assertEquals(1, rootScope.getChildren().size());

		new ScopeMoveLeftAction(lastChild.getId()).execute(context);

		assertEquals(0, middle.getChildren().size());
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(middle, rootScope.getChildren().get(0));
		assertEquals(lastChild, rootScope.getChildren().get(1));
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(middle.getChildren().get(0), lastChild);
		assertEquals(1, middle.getChildren().size());
		assertEquals(1, rootScope.getChildren().size());

		final ScopeMoveLeftAction moveLeftScopeAction = new ScopeMoveLeftAction(lastChild.getId());
		final ModelAction rollbackAction = moveLeftScopeAction.execute(context);

		assertEquals(0, middle.getChildren().size());
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(middle, rootScope.getChildren().get(0));
		assertEquals(lastChild, rootScope.getChildren().get(1));

		rollbackAction.execute(context);

		assertEquals(middle.getChildren().get(0), lastChild);
		assertEquals(1, middle.getChildren().size());
		assertEquals(1, rootScope.getChildren().size());
	}
}
