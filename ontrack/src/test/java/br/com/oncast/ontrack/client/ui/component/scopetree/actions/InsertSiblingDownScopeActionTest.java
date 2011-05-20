package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.beans.Scope;

public class InsertSiblingDownScopeActionTest {

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

	@Test
	public void siblingDownMustBeDown() throws UnableToCompleteActionException {
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getIndex() + 1), lastChild);
		assertEquals(2, rootScope.getChildren().size());
		final InsertSiblingDownScopeAction insertSiblingDownScopeAction = new InsertSiblingDownScopeAction(firstChild);
		insertSiblingDownScopeAction.execute();
		assertEquals(3, rootScope.getChildren().size());
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getIndex() + 1), insertSiblingDownScopeAction.getScope());
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getIndex() + 2), lastChild);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void rootCantAddSiblingDown() throws UnableToCompleteActionException {
		new InsertSiblingDownScopeAction(rootScope).execute();
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getIndex() + 1), lastChild);
		assertEquals(2, rootScope.getChildren().size());
		final InsertSiblingDownScopeAction insertSiblingDownScopeAction = new InsertSiblingDownScopeAction(firstChild);
		insertSiblingDownScopeAction.execute();
		assertEquals(3, rootScope.getChildren().size());
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getIndex() + 1), insertSiblingDownScopeAction.getScope());
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getIndex() + 2), lastChild);
		insertSiblingDownScopeAction.rollback();
		assertEquals(firstChild.getParent().getChildren().get(firstChild.getIndex() + 1), lastChild);
		assertEquals(2, rootScope.getChildren().size());
	}

}
