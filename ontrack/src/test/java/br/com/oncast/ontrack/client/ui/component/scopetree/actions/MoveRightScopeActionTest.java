package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.scope.exceptions.UnableToCompleteActionException;

public class MoveRightScopeActionTest {

	private Scope rootScope;
	private Scope firstChild;
	private Scope lastChild;

	@Before
	public void setUp() {
		rootScope = new Scope("root");
		firstChild = new Scope("first");
		lastChild = new Scope("last");
		rootScope.add(firstChild);
		rootScope.add(lastChild);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootCantbeMovedRight() throws UnableToCompleteActionException {
		new ScopeMoveLeftAction(rootScope).execute();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void aScopeCantBeMovedIfDontHaveUpSibling() throws UnableToCompleteActionException {
		new ScopeMoveLeftAction(firstChild).execute();
	}

	@Test
	public void aScopeMovedToRightMustChangeToChildOfUpSibling() throws UnableToCompleteActionException {
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(firstChild, rootScope.getChildren().get(0));
		assertEquals(lastChild, rootScope.getChildren().get(1));
		new ScopeMoveRightAction(lastChild).execute();
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
		final ScopeMoveRightAction moveRightScopeAction = new ScopeMoveRightAction(lastChild);
		moveRightScopeAction.execute();
		assertEquals(1, rootScope.getChildren().size());
		assertEquals(1, firstChild.getChildren().size());
		assertEquals(firstChild, rootScope.getChildren().get(0));
		assertEquals(lastChild, firstChild.getChildren().get(0));
		moveRightScopeAction.rollback();
		assertEquals(2, rootScope.getChildren().size());
		assertEquals(0, firstChild.getChildren().size());
		assertEquals(firstChild, rootScope.getChildren().get(0));
		assertEquals(lastChild, rootScope.getChildren().get(1));
	}
}
