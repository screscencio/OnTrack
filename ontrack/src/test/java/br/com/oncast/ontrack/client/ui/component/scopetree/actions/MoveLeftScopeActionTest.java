package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.beans.Scope;

public class MoveLeftScopeActionTest {

	private Scope rootScope;
	private Scope middle;
	private Scope lastChild;

	@Before
	public void setUp() {
		rootScope = new Scope("root");
		middle = new Scope("middle");
		lastChild = new Scope("last");
		rootScope.add(middle);
		middle.add(lastChild);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootCantbeMovedLeft() throws UnableToCompleteActionException {
		new MoveLeftScopeAction(rootScope).execute();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void aRootChildCantbeMovedLeft() throws UnableToCompleteActionException {
		assertEquals(rootScope.getChildren().get(0), middle);
		new MoveLeftScopeAction(middle).execute();
	}

	@Test
	public void aChildMovedLeftMustChangeToASibling() throws UnableToCompleteActionException {
		assertEquals(middle.getChildren().get(0), lastChild);
		assertEquals(1, middle.getChildren().size());
		assertEquals(1, rootScope.getChildren().size());
		new MoveLeftScopeAction(lastChild).execute();
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
		final MoveLeftScopeAction moveLeftScopeAction = new MoveLeftScopeAction(lastChild);
		moveLeftScopeAction.execute();
		assertEquals(0, middle.getChildren().size());
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(middle, rootScope.getChildren().get(0));
		assertEquals(lastChild, rootScope.getChildren().get(1));
		moveLeftScopeAction.rollback();
		assertEquals(middle.getChildren().get(0), lastChild);
		assertEquals(1, middle.getChildren().size());
		assertEquals(1, rootScope.getChildren().size());
	}
}
