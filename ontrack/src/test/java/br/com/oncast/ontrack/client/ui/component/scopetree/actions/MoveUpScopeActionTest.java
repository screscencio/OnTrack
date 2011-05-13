package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.beans.Scope;

public class MoveUpScopeActionTest {
	private Scope rootScope;
	private Scope firstChild;
	private Scope lastChild;

	@Before
	public void setUp() {
		rootScope = new Scope("root");
		firstChild = new Scope("child");
		lastChild = new Scope("last");
		rootScope.add(firstChild);
		rootScope.add(lastChild);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootCantbeMovedUp() throws UnableToCompleteActionException {
		new MoveUpScopeAction(rootScope).execute();
	}

	@Test
	public void movedDownScopeMustBeDown() throws UnableToCompleteActionException {
		assertEquals(rootScope.getChildren().get(0), firstChild);
		assertEquals(rootScope.getChildren().get(1), lastChild);
		final MoveUpScopeAction moveUp = new MoveUpScopeAction(lastChild);
		moveUp.execute();
		assertEquals(rootScope.getChildren().get(0), lastChild);
		assertEquals(rootScope.getChildren().get(1), firstChild);
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(rootScope.getChildren().get(0), firstChild);
		assertEquals(rootScope.getChildren().get(1), lastChild);
		final MoveUpScopeAction moveDown = new MoveUpScopeAction(lastChild);
		moveDown.execute();
		assertEquals(rootScope.getChildren().get(0), lastChild);
		assertEquals(rootScope.getChildren().get(1), firstChild);
		moveDown.rollback();
		assertEquals(rootScope.getChildren().get(0), firstChild);
		assertEquals(rootScope.getChildren().get(1), lastChild);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void lastNodeCantBeMovedDown() throws UnableToCompleteActionException {
		new MoveUpScopeAction(firstChild).execute();
	}
}
