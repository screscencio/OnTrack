package br.com.oncast.ontrack.shared.model.action;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;

public class ScopeMoveUpActionTest {
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

		context = ProjectTestUtils.createProjectContext(rootScope, ReleaseFactoryTestUtil.create("Project"));
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootCantbeMovedUp() throws UnableToCompleteActionException {
		new ScopeMoveUpAction(rootScope.getId()).execute(context);
	}

	@Test
	public void movedDownScopeMustBeDown() throws UnableToCompleteActionException {
		assertEquals(rootScope.getChildren().get(0), firstChild);
		assertEquals(rootScope.getChildren().get(1), lastChild);

		final ScopeMoveUpAction moveUp = new ScopeMoveUpAction(lastChild.getId());
		moveUp.execute(context);

		assertEquals(rootScope.getChildren().get(0), lastChild);
		assertEquals(rootScope.getChildren().get(1), firstChild);
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(rootScope.getChildren().get(0), firstChild);
		assertEquals(rootScope.getChildren().get(1), lastChild);

		final ScopeMoveUpAction moveDown = new ScopeMoveUpAction(lastChild.getId());
		final ModelAction rollbackAction = moveDown.execute(context);

		assertEquals(rootScope.getChildren().get(0), lastChild);
		assertEquals(rootScope.getChildren().get(1), firstChild);

		rollbackAction.execute(context);

		assertEquals(rootScope.getChildren().get(0), firstChild);
		assertEquals(rootScope.getChildren().get(1), lastChild);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void lastNodeCantBeMovedDown() throws UnableToCompleteActionException {
		new ScopeMoveUpAction(firstChild.getId()).execute(context);
	}
}
