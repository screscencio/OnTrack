package br.com.oncast.ontrack.client.ui.component.scopetree.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.beans.Scope;

public class InsertFatherScopeActionTest {

	private Scope rootScope;
	private Scope childScope;

	@Before
	public void setUp() {
		rootScope = new Scope("root");
		childScope = new Scope("child");
		rootScope.add(childScope);
	}

	@Test
	public void mustInsertAScopeAsFather() throws UnableToCompleteActionException {
		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);
		final InsertFatherScopeAction insertFatherScopeAction = new InsertFatherScopeAction(childScope);
		insertFatherScopeAction.execute();
		assertEquals(childScope.getParent(), insertFatherScopeAction.getScope());
		assertEquals(rootScope.getChildren().get(0), insertFatherScopeAction.getScope());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void insertingFatherAtRootNodeMustThrowException() throws UnableToCompleteActionException {
		new InsertFatherScopeAction(rootScope).execute();
	}

	@Test
	public void rollbackMustRevertExecuteChanges() throws UnableToCompleteActionException {
		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);
		final InsertFatherScopeAction insertFatherScopeAction = new InsertFatherScopeAction(childScope);
		insertFatherScopeAction.execute();
		assertEquals(childScope.getParent(), insertFatherScopeAction.getScope());
		assertEquals(rootScope.getChildren().get(0), insertFatherScopeAction.getScope());
		insertFatherScopeAction.rollback();
		assertEquals(childScope.getParent(), rootScope);
		assertEquals(rootScope.getChildren().get(0), childScope);
	}
}
